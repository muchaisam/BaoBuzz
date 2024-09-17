package com.msdc.baobuzz.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data Models
@Entity
data class Coach(
    @PrimaryKey val id: Int,
    val name: String,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val photo: String,
    @TypeConverters(TeamConverter::class)
    val team: TeamManaged,
    @TypeConverters(CareerStepListConverter::class)
    val career: List<CareerStep>,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class TeamManaged(
    val id: Int,
    val name: String,
    val logo: String
)

data class CareerStep(
    val team: TeamManaged,
    val start: String,
    val end: String?,
)


// API Response Models
data class ApiResponse<T>(
    val get: String,
    val parameters: Map<String, String>,
    val errors: List<String>,
    val results: Int,
    val paging: CoachPaging,
    val response: List<T>
)

data class CoachPaging(
    val current: Int,
    val total: Int
)

class TeamConverter {
    @TypeConverter
    fun fromTeam(team: TeamManaged): String = Gson().toJson(team)

    @TypeConverter
    fun toTeam(teamString: String): TeamManaged = Gson().fromJson(teamString, TeamManaged::class.java)
}

class CareerStepListConverter {
    @TypeConverter
    fun fromCareerStepList(careerSteps: List<CareerStep>): String = Gson().toJson(careerSteps)

    @TypeConverter
    fun toCareerStepList(careerStepsString: String): List<CareerStep> {
        val listType = object : TypeToken<List<CareerStep>>() {}.type
        return Gson().fromJson(careerStepsString, listType)
    }
}
