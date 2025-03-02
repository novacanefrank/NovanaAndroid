package com.example.novana.model

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    var UserId:String="",
    var Firstname:String="",
    var Lastname:String="",
    var Address:String="",
    var Contact:String="",
    var Email:String="",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readString()?:"",
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(UserId)
        parcel.writeString(Firstname)
        parcel.writeString(Lastname)
        parcel.writeString(Address)
        parcel.writeString(Contact)
        parcel.writeString(Email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}