package com.forwork.triolan.soap;

import java.util.ArrayList;
// Класс создан в виде структуры JSON объекта который парсится наложение данных JSON на элементы данного класса
public class GetCustomerPlaylistData {
    public GetCustomerPlaylistDataD d = new GetCustomerPlaylistDataD();

    public static class GetCustomerPlaylistDataD {
        public String __type;
        public int CustomerStatus;
        public String Status;
        ChannelInfo channelInfo = new ChannelInfo();

        public ArrayList<ChannelInfo> Channels = new ArrayList<ChannelInfo>();

        public GetCustomerPlaylistDataD() {

            Channels.add(channelInfo);

        }

        public static class ChannelInfo {

            public int ID;
            public short EPGID;
            public String Name;
            public String SmallDescription;
            public String FullDescription;
            public String Logo;
            public double DayPrice;
            public double MonthPrice;
            public boolean DayPriceExists;
            public boolean MonthPriceExists;
            public boolean Subscribed;
            public String StartTime;
            public String EndTime;
            public boolean InPackageOnly;
            public boolean IsFree;
            public String Stream;
            public boolean IsAction;

        }
    }
}