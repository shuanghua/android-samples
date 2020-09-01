package com.shuanghua.retrofit.bean;

import java.util.List;

public class TestBean {

    private List<HostSearchBean> hostSearch;

    public List<HostSearchBean> getHostSearch() {
        return hostSearch;
    }

    public void setHostSearch(List<HostSearchBean> hostSearch) {
        this.hostSearch = hostSearch;
    }

    public static class HostSearchBean {
        /**
         * advertImg :
         * advertType : html5
         * advertParam : 546456546
         * advertTargetId :
         * advertSecondTargetId :
         * advertExtend : {}
         * advertStyle :
         * advertTitle : 54645645645645645
         * advertTable : search_word
         * advertShowNavTitle :
         * id : 74
         * keyword : 54645645645645645
         * image :
         * searchWordSpecialStyle : {"fontColor":"#D92B3A","fontColorClarity":100,"backgroundColor":"#1AD92B3A","backgroundColorClarity":0.1}
         */

        private String advertImg;
        private String advertType;
        private String advertParam;
        private String advertTargetId;
        private String advertSecondTargetId;
        private AdvertExtendBean advertExtend;
        private String advertStyle;
        private String advertTitle;
        private String advertTable;
        private String advertShowNavTitle;
        private int id;
        private String keyword;
        private String image;
        private SearchWordSpecialStyleBean searchWordSpecialStyle;

        public String getAdvertImg() {
            return advertImg;
        }

        public void setAdvertImg(String advertImg) {
            this.advertImg = advertImg;
        }

        public String getAdvertType() {
            return advertType;
        }

        public void setAdvertType(String advertType) {
            this.advertType = advertType;
        }

        public String getAdvertParam() {
            return advertParam;
        }

        public void setAdvertParam(String advertParam) {
            this.advertParam = advertParam;
        }

        public String getAdvertTargetId() {
            return advertTargetId;
        }

        public void setAdvertTargetId(String advertTargetId) {
            this.advertTargetId = advertTargetId;
        }

        public String getAdvertSecondTargetId() {
            return advertSecondTargetId;
        }

        public void setAdvertSecondTargetId(String advertSecondTargetId) {
            this.advertSecondTargetId = advertSecondTargetId;
        }

        public AdvertExtendBean getAdvertExtend() {
            return advertExtend;
        }

        public void setAdvertExtend(AdvertExtendBean advertExtend) {
            this.advertExtend = advertExtend;
        }

        public String getAdvertStyle() {
            return advertStyle;
        }

        public void setAdvertStyle(String advertStyle) {
            this.advertStyle = advertStyle;
        }

        public String getAdvertTitle() {
            return advertTitle;
        }

        public void setAdvertTitle(String advertTitle) {
            this.advertTitle = advertTitle;
        }

        public String getAdvertTable() {
            return advertTable;
        }

        public void setAdvertTable(String advertTable) {
            this.advertTable = advertTable;
        }

        public String getAdvertShowNavTitle() {
            return advertShowNavTitle;
        }

        public void setAdvertShowNavTitle(String advertShowNavTitle) {
            this.advertShowNavTitle = advertShowNavTitle;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getKeyword() {
            return keyword;
        }

        public void setKeyword(String keyword) {
            this.keyword = keyword;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public SearchWordSpecialStyleBean getSearchWordSpecialStyle() {
            return searchWordSpecialStyle;
        }

        public void setSearchWordSpecialStyle(SearchWordSpecialStyleBean searchWordSpecialStyle) {
            this.searchWordSpecialStyle = searchWordSpecialStyle;
        }

        public static class AdvertExtendBean {
        }

        public static class SearchWordSpecialStyleBean {
            /**
             * fontColor : #D92B3A
             * fontColorClarity : 100
             * backgroundColor : #1AD92B3A
             * backgroundColorClarity : 0.1
             */

            private String fontColor;
            private int fontColorClarity;
            private String backgroundColor;
            private double backgroundColorClarity;

            public String getFontColor() {
                return fontColor;
            }

            public void setFontColor(String fontColor) {
                this.fontColor = fontColor;
            }

            public int getFontColorClarity() {
                return fontColorClarity;
            }

            public void setFontColorClarity(int fontColorClarity) {
                this.fontColorClarity = fontColorClarity;
            }

            public String getBackgroundColor() {
                return backgroundColor;
            }

            public void setBackgroundColor(String backgroundColor) {
                this.backgroundColor = backgroundColor;
            }

            public double getBackgroundColorClarity() {
                return backgroundColorClarity;
            }

            public void setBackgroundColorClarity(double backgroundColorClarity) {
                this.backgroundColorClarity = backgroundColorClarity;
            }
        }
    }
}
