package com.example.travelmantics;

import android.os.Parcel;
import android.os.Parcelable;

public class TravelDeal implements Parcelable {
    private String id;
    private String title;
    private String price;
    private String description;
    private String imageUrl;
    private String imageName;

    public TravelDeal(String id, String title, String price, String description, String imageUrl, String imageName) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.imageName = imageName;
    }

    public TravelDeal() {
    }


    protected TravelDeal(Parcel in) {
        id = in.readString();
        title = in.readString();
        price = in.readString();
        description = in.readString();
        imageUrl = in.readString();
        imageName = in.readString();
    }

    public static final Creator<TravelDeal> CREATOR = new Creator<TravelDeal>() {
        @Override
        public TravelDeal createFromParcel(Parcel in) {
            return new TravelDeal(in);
        }

        @Override
        public TravelDeal[] newArray(int size) {
            return new TravelDeal[size];
        }
    };

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(price);
        parcel.writeString(description);
        parcel.writeString(imageUrl);
        parcel.writeString(imageName);
    }
}
