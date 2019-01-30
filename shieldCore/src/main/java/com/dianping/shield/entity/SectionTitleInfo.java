package com.dianping.shield.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SectionTitleInfo implements Parcelable {
    public static final Creator<SectionTitleInfo> CREATOR = new Creator<SectionTitleInfo>() {
        @Override
        public SectionTitleInfo createFromParcel(Parcel source) {
            return new SectionTitleInfo(source);
        }

        @Override
        public SectionTitleInfo[] newArray(int size) {
            return new SectionTitleInfo[size];
        }
    };
    public String cellKey;
    public int section;
    public String sectionTitle;

    public SectionTitleInfo(String cellKey, int section, String sectionTitle) {
        this.cellKey = cellKey;
        this.section = section;
        this.sectionTitle = sectionTitle;
    }

    public SectionTitleInfo() {
    }

    protected SectionTitleInfo(Parcel in) {
        this.cellKey = in.readString();
        this.section = in.readInt();
        this.sectionTitle = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.cellKey);
        dest.writeInt(this.section);
        dest.writeString(this.sectionTitle);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SectionTitleInfo that = (SectionTitleInfo) o;

        if (section != that.section) return false;
        if (cellKey != null ? !cellKey.equals(that.cellKey) : that.cellKey != null)
            return false;
        return sectionTitle != null ? sectionTitle.equals(that.sectionTitle) : that.sectionTitle == null;
    }

    @Override
    public int hashCode() {
        int result = cellKey != null ? cellKey.hashCode() : 0;
        result = 31 * result + section;
        result = 31 * result + (sectionTitle != null ? sectionTitle.hashCode() : 0);
        return result;
    }
}
