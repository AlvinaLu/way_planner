package cz.cvut.fel.lushnalv.data

import androidx.lifecycle.LiveData
import cz.cvut.fel.lushnalv.data.dto.response.*
import cz.cvut.fel.lushnalv.models.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DayPointRepository(
    private val userDao: UserDao,
    private val tripDao: TripDao,
    private val dayDao: DayDao,
    private val dayPointDao: DayPointDao,
    private val dutyDao: DutyDao,
    private val dutyWithUsersDao: DutyWithUsersDao,
    private val commentDao: CommentDao,
    private val appWebApi: AppWebApi
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    fun insertDayPoint(vararg dayPoint: DayPoint) {
        coroutineScope.launch(Dispatchers.IO) {
            dayPointDao.insetDayPoint(*dayPoint)
        }
    }

    fun deleteAll() {
        coroutineScope.launch(Dispatchers.IO) {
            dayPointDao.deleteAll()
        }
    }

    fun findDayPointWithDutiesById(dayPointId: Long): LiveData<DayPointWithDutiesAndComments> {
        return dayPointDao.findDayPointsAndDutiesById(dayPointId)
    }

    fun findDayPointWithDutiesAndUsersById(dayPointId: Long): LiveData<List<DutyWithUsers>> {
        val duties = dutyDao.getAllDutiesWithUsersByDayId(dayPointId)
        return duties
    }

    fun findDayPointById(dayPointId: Long): LiveData<DayPoint> {
        return dayPointDao.findDayPointById(dayPointId)
    }


    suspend fun getDayPointFromWeb(dayPointId: Long, token: String): Boolean {
        val response = appWebApi.getDayPoint(dayPointId, token).blockingGet()
        saveData(response)
        return true

    }

    suspend fun dayPointAddPhotos(newPhotoList: NewPhotoListDto, token: String): Boolean {
        val response = appWebApi.dayPointAddPhotos(newPhotoList, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun dayPointAddDocument(newDocumented: NewDocumentDto, token: String): Boolean {
        val response = appWebApi.dayPointAddDocument(newDocumented, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun createNewExpense(newDuty: NewDutyDto, token: String): Boolean {
        val response = appWebApi.createNewDuty(newDuty, token).blockingGet()
        saveData(response)
        return true
    }

    suspend fun deleteDocument(
        deleteDocumentDto: DeleteDocumentDto,
        token: String
    ): DeleteDocumentDto {
        val response =  appWebApi.deleteDayPointDocument(deleteDocumentDto, token).blockingGet()
        val dayPoint = dayPointDao.findDayPoint(response.dayPointId)
        dayPoint.setDocumentList(dayPoint.getDocumentList().filter { it!= response.url })
        dayPointDao.insetDayPoint(dayPoint)
        return response
    }

    suspend fun deleteImage(
        deleteImageDto: DeleteImageDto,
        token: String
    ): DeleteImageDto {
        val response =  appWebApi.deleteDayPointImage(deleteImageDto, token).blockingGet()
        val dayPoint = dayPointDao.findDayPoint(response.dayPointId)
        dayPoint.setPhotoList(dayPoint.getPhotoList().filter { it!= response.url })
        dayPointDao.insetDayPoint(dayPoint)
        return response
    }

    suspend fun createNewComment(newCommentDto: NewCommentDto, token: String): Boolean {
        val response = appWebApi.createNewComment(newCommentDto, token).blockingGet()
        val comment = Comment(
            commentId = response.id,
            message = response.message,
            date = response.date,
            author = response.author,
            dayPointId = response.dayPointId
        )
        commentDao.insertComments(comment)
        return true
    }


    suspend fun deleteComment(commentId: Long, token: String): Boolean {
        val response = appWebApi.deleteComment(commentId, token).blockingGet()
        commentDao.delete(response)
        return true
    }


    suspend fun deleteDuty(dutyId: Long, token: String): Boolean {
        val response = appWebApi.deleteDuty(dutyId, token).blockingGet()
        dutyWithUsersDao.delete(response)
        dutyDao.delete(response)
        return true
    }

    private suspend fun saveData(response: DayPointWholeInfoDto) {

        val dayPoint = DayPoint(
            dayPointId = response.id,
            title = response.title,
            date = response.date,
            duration = response.duration,
            latitude = response.latitude,
            longitude = response.longitude,
            typeOfDayPoint = response.typeOfDayPoint,
            dayId = response.dayId,
            defaultPhoto = response.defaultPhoto,
            travelTime = response.travelTime,
            travelType = response.travelType,
            travelDistance = response.travelDistance,
            openingMessage = response.openingMessage
        )
        dayPoint.setPhotoList(response.photoListString)
        dayPoint.setDocumentList(response.documentListString)
        val listOfDuty = mutableListOf<Duty>()

        response.duties.onEach{
            if(it.deleted){
                dutyWithUsersDao.delete(it.dutyId)
                dutyDao.delete(it.dutyId)
            }
        }.filter { !it.deleted }.forEach {
            listOfDuty.add(
                Duty(
                    title = it.title,
                    dutyId = it.dutyId,
                    author = it.author,
                    amount = it.amount,
                    currency = it.currency,
                    dayPointId = it.dayPointId
                )
            )
        }

        val listOfUsers = mutableListOf<User>()
        response.users.forEach {
            listOfUsers.add(
                User(
                    userId = it.id,
                    email = it.email,
                    name = it.name,
                    imgUrl = it.imgUrl
                )
            )
        }

        val listOfComments = mutableListOf<Comment>()
        response.comments.onEach{
            if(it.deleted){
                commentDao.delete(it.id)
            }
        }.filter { !it.deleted }.forEach {
            listOfComments.add(
                Comment(
                    commentId = it.id,
                    message = it.message,
                    date = it.date,
                    author = it.author,
                    dayPointId = it.dayPointId
                )
            )
        }
        dayPointDao.insetDayPoint(dayPoint)
        commentDao.insertComments(*listOfComments.toTypedArray())
        dutyDao.insertDuties(*listOfDuty.toTypedArray())
        userDao.insertUser(*listOfUsers.toTypedArray())

        response.duties.filter { !it.deleted }.forEach{ dutyDto ->
            dutyWithUsersDao.insert(UserDutyCrossRef(dutyDto.dutyId, dutyDto.author))
            dutyDto.users.forEach {
                dutyWithUsersDao.insert(UserDutyCrossRef(dutyDto.dutyId, it))
            }
        }
    }
}