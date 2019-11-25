package forexmanagement;

import com.sun.org.apache.xpath.internal.operations.Mod;

public class Strategies {
    static public class Strategy1{
        static public String MA_NOT_CROSS_YET = "MA not cross yet";
        static public String MA_ALREADY_CROSSED = "MA crossed already";
        static public String NOT_IN_TREND = "Out of the trend";
        static public String UNDEFINED = "Undefined";

        static public String TREND_UP = "Trend up";
        static public String TREND_DOWN = "Trend down";
        static public String TREND_UNDEFINED = "Trend undefined";

        public static class Model{
            private float SMA50;
            private float SMA150;
            private float EMA10;

            public Model(float SMA50, float SMA150, float EMA10) {
                this.SMA50 = SMA50;
                this.SMA150 = SMA150;
                this.EMA10 = EMA10;
            }

            public float getSMA50() {
                return SMA50;
            }

            public float getSMA150() {
                return SMA150;
            }

            public float getEMA10() {
                return EMA10;
            }
        }

        private Model model;

        Strategy1(Model model){
            this.model = model;
        }

        public String checkStatus(){
            // trend up
            if (checkTrend().equals(TREND_UP)){
                if (model.getEMA10() <= model.getSMA50()){
                    return MA_NOT_CROSS_YET;
                }else {
                    return NOT_IN_TREND;
                }
            }else if (checkTrend().equals(TREND_DOWN)){
                if (model.getEMA10() >= model.getSMA50()){
                    return MA_NOT_CROSS_YET;
                }else {
                    return NOT_IN_TREND;
                }
            }else {
                return UNDEFINED;
            }
        }

        public String checkTrend(){
            if (model.getSMA50() <= model.getSMA150() && model.getEMA10() <= model.getSMA150()){
                return TREND_DOWN;
            }else if (model.getSMA50() >= model.getSMA150() && model.getEMA10() >= model.getSMA150()){
                return TREND_UP;
            }else {
                return TREND_UNDEFINED;
            }
        }
    }


}
