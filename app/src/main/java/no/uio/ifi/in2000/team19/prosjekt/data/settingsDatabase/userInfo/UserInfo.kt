package no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(

    @PrimaryKey(autoGenerate = false) val id: Int,

    //UserInfo
    @ColumnInfo(name = "userName") var userName: String,
    @ColumnInfo(name = "dogName") var dogName: String,
    //Age
    @ColumnInfo(name = "isSenior") var isSenior: Boolean,
    @ColumnInfo(name = "isAdult ") var isAdult: Boolean,
    @ColumnInfo(name = "isPuppy") var isPuppy: Boolean,

    //Nose
    @ColumnInfo(name = "isFlatNosed") var isFlatNosed: Boolean,
    @ColumnInfo(name = "isNormalNosed") var isNormalNosed: Boolean,

    //Body
    @ColumnInfo(name = "isThinBody") var isThin: Boolean,
    @ColumnInfo(name = "isMediumBody") var isMediumBody: Boolean,
    @ColumnInfo(name = "isThickBody") var isThickBody: Boolean,
    //Fur
    @ColumnInfo(name = "isLongHaired") var isLongHaired: Boolean,
    @ColumnInfo(name = "isShortHaired") var isShortHaired: Boolean,

    @ColumnInfo(name = "isThinHaired") var isThinHaired: Boolean,
    @ColumnInfo(name = "isThickhaired") var isThickHaired: Boolean,

    @ColumnInfo(name = "isLightHaired") var isLightHaired: Boolean,
    @ColumnInfo(name = "isDarkHaired") var isDarkHaired: Boolean,


    )
