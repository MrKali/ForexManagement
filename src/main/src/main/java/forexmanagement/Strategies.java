package forexmanagement;

public class Strategies {
    static public String TREND_UP = "Trend up";
    static public String TREND_DOWN = "Trend down";

    static public class Strategy1{
        static String MA_NOT_CROSS_YET = "MA not cross yet";
        static String NOT_IN_TREND = "Out of the trend";
        static String UNDEFINED = "Undefined";

        public static class Model{
            private float SMA50;
            private float SMA150;
            private float EMA10;

            public Model(float SMA50, float SMA150, float EMA10) {
                this.SMA50 = SMA50;
                this.SMA150 = SMA150;
                this.EMA10 = EMA10;
            }

            float getSMA50() {
                return SMA50;
            }

            float getSMA150() {
                return SMA150;
            }

            float getEMA10() {
                return EMA10;
            }
        }

        private Model model;

        public Strategy1(Model model){
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
                return "Trend undefined";
            }
        }
    }


}
