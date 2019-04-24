package net.kaicong.ipcam.bean;

import net.kaicong.ipcam.R;
import android.content.Context;


/**
 * Created by LingYan on 2014/11/29 0029.
 */
public class GetCameraModel {

    public static final int CAMERA_MODEL_ZHIYUN = 0;
    public static final int CAMERA_MODEL_SIP1018 = 1018;
    public static final int CAMERA_MODEL_SIP1303 = 1303;
    public static final int CAMERA_MODEL_SIP1601 = 1601;
    public static final int CAMERA_MODEL_SIP1201 = 1201;
    public static final int CAMERA_MODEL_SIP1211 = 1211;
    public static final int CAMERA_MODEL_SIP1406 = 1406;
    public static final int CAMERA_MODEL_SIP1120 = 1120;

    /**
     * 获取该型号摄像机所在的型号
     *
     * @param mModel
     * @return
     */
    public static int getCameraModel(int mModel) {
        int resultModel = 0;
        switch (mModel) {
            /**
             * 1018系列
             */
            case CameraModel.MODEL_ID_1018_428:
            case CameraModel.MODEL_ID_1018_1017:
            case CameraModel.MODEL_ID_1018_1018:
            case CameraModel.MODEL_ID_1018_10180000:
            case CameraModel.MODEL_ID_1018_10180011:
            case CameraModel.MODEL_ID_1018_1019:
            case CameraModel.MODEL_ID_1018_1020:
            case CameraModel.MODEL_ID_1018_1021:
            case CameraModel.MODEL_ID_1018_1022:
            case CameraModel.MODEL_ID_1018_902:
            case CameraModel.MODEL_ID_1018_910:
                resultModel = CAMERA_MODEL_SIP1018;
                break;

            /**
             * 1303系列
             */
            case CameraModel.MODEL_ID_1303_1303:
            case CameraModel.MODEL_ID_1303_1305:
                resultModel = CAMERA_MODEL_SIP1303;
                break;

            /**
             * 1601系列
             */
            case CameraModel.MODEL_ID_1601_1601:
            case CameraModel.MODEL_ID_1601_1602:
            case CameraModel.MODEL_ID_1601_1603:
            case CameraModel.MODEL_ID_1601_1604:
            case CameraModel.MODEL_ID_1601_1605:
            case CameraModel.MODEL_ID_1601_1606:
            case CameraModel.MODEL_ID_1601_16060000:
            case CameraModel.MODEL_ID_1601_913:
                resultModel = CAMERA_MODEL_SIP1601;
                break;

            /**
             * 1201系列
             */
            case CameraModel.MODEL_ID_1201_1201:
            case CameraModel.MODEL_ID_1201_1202:
            case CameraModel.MODEL_ID_1201_12020000:
            case CameraModel.MODEL_ID_1201_1203:
            case CameraModel.MODEL_ID_1201_1204:
            case CameraModel.MODEL_ID_1201_1205:
            case CameraModel.MODEL_ID_1201_1206:
            case CameraModel.MODEL_ID_1201_1207:
            case CameraModel.MODEL_ID_1201_1210:
            case CameraModel.MODEL_ID_1201_1213:
            case CameraModel.MODEL_ID_1201_1214:
            case CameraModel.MODEL_ID_1201_1215:
            case CameraModel.MODEL_ID_1201_914:
            case CameraModel.MODEL_ID_1201_1212:
                resultModel = CAMERA_MODEL_SIP1201;
                break;

            /**
             * 1211系列
             */
            case CameraModel.MODEL_ID_1211_1211:
                resultModel = CAMERA_MODEL_SIP1211;
                break;

            /**
             * 1404系列
             */
            case CameraModel.MODEL_ID_1406_1306:
            case CameraModel.MODEL_ID_1406_13060000:
            case CameraModel.MODEL_ID_1406_1406:
            case CameraModel.MODEL_ID_1406_916:
            case CameraModel.MODEL_ID_1406_918:
                resultModel = CAMERA_MODEL_SIP1406;
                break;

            /**
             * 1120系列
             */
            case CameraModel.MODEL_ID_1120_1113:
            case CameraModel.MODEL_ID_1120_1118:
            case CameraModel.MODEL_ID_1120_1119:
            case CameraModel.MODEL_ID_1120_1120:
            case CameraModel.MODEL_ID_1120_1121:
            case CameraModel.MODEL_ID_1120_1128:
            case CameraModel.MODEL_ID_1120_1308:
            case CameraModel.MODEL_ID_1120_264:
            case CameraModel.MODEL_ID_1120_912:
                resultModel = CAMERA_MODEL_SIP1120;
                break;

            /**
             * 智云系列
             */
            case CameraModel.MODEL_ID_ZHIYUN:
                resultModel = CAMERA_MODEL_ZHIYUN;
                break;
            default:
                resultModel = -1;
                break;

        }
        return resultModel;
    }

    public static String getCameraName(Context context, int modelId) {
        String resultUrl = "";
        /**
         *信息采集
         */
        switch (modelId) {
            /**
             *B系列的IPCAM SIP1018技术方案
             *sip428                  428                 SIP1018
             *sip1017                 1017                SIP1018
             *sip1018                 1018                SIP1018
             *sip1018w                10180000            SIP1018
             *sip1018l                10180011            SIP1018
             *sip1019                 1019                SIP1018
             *sip1020                 1020                SIP1018
             *sip1021                 1021                SIP1018
             *sip1022                 1022                SIP1018
             *                        902                 SIP1018         //902是为了兼容老版本
             *                        910                 SIP1018         //910是为了兼容老版本
             */
            case 428:
                resultUrl = "Sip428";
                break;
            case 1017:
                resultUrl = "Sip1017";
                break;
            case 1018:
                resultUrl = "Sip1018";
                break;
            case 10180000:
                resultUrl = "Sip1018W";
                break;
            case 10180011:
                resultUrl = "Sip1018L";
                break;
            case 1019:
                resultUrl = "Sip1019";
                break;
            case 1020:
                resultUrl = "Sip1020";
                break;
            case 1021:
                resultUrl = "Sip1021";
                break;
            case 1022:
                resultUrl = "Sip1022";
                break;
            case 902://902是为了兼容老版本
                resultUrl = "Sip902";
                break;
            case 910://910是为了兼容老版本
                resultUrl = "Sip910";
                break;

            /**
             *F系列的IPCAM SIP1406技术方案
             *sip1306                 1306                SIP1406
             *sip1306w                13060000            SIP1406
             *sip1406                 1406                SIP1406
             *                        916                 SIP1406         //916是为了兼容老版本
             *                        918                 SIP1406         //918是为了兼容老版本
             */
            case 1306:
                resultUrl = "Sip1306";
                break;
            case 13060000:
                resultUrl = "Sip1306W";
                break;
            case 1406:
                resultUrl = "Sip1406";
                break;
            case 916://916是为了兼容老版本
                resultUrl = "Sip916";
                break;
            case 918://918是为了兼容老版本
                resultUrl = "Sip918";
                break;

            /**
             *H系列的IPCAM SIP1120技术方案
             *sip264                  264                 SIP1120
             *sip1113                 1113                SIP1120
             *sip1118                 1118                SIP1120
             *sip1119                 1119                SIP1120
             *sip1120                 1120                SIP1120
             *sip1121                 1121                SIP1120
             *sip1128                 1128                SIP1120
             *sip1308                 1308                SIP1120
             *                        912                 SIP1120         //912是为了兼容老版本
             */
            case 264:
                resultUrl = "Sip264";
                break;
            case 1113:
                resultUrl = "Sip1113";
                break;
            case 1118:
                resultUrl = "Sip1118";
                break;
            case 1119:
                resultUrl = "Sip1119";
                break;
            case 1120:
                resultUrl = "Sip1120";
                break;
            case 1121:
                resultUrl = "Sip1121";
                break;
            case 1128:
                resultUrl = "Sip1128";
                break;
            case 1308:
                resultUrl = "Sip1308";
                break;
            case 912://912是为了兼容老版本
                resultUrl = "Sip912";
                break;

            /**
             *M系列的IPCAM SIP1201技术方案
             *sip1201                 1201                SIP1201
             *sip1202w                12020000            SIP1201
             *sip1203                 1203                SIP1201
             *sip1204                 1204                SIP1201
             *sip1205                 1205                SIP1201
             *sip1206                 1206                SIP1201
             *sip1207                 1207                SIP1201
             *sip1210                 1210                SIP1201
             *sip1213                 1213                SIP1201
             *sip1214                 1214                SIP1201
             *sip1215                 1215                SIP1201
             *sip1202w                1202                SIP1201
             *                        914                 SIP1201         //914是为了兼容老版本
             */

            case 1201:
                resultUrl = "Sip1201";
                break;
            case 1202:
                resultUrl = "Sip1202";
                break;
            case 12020000:
                resultUrl = "Sip1202W";
                break;
            case 1203:
                resultUrl = "Sip1203";
                break;
            case 1204:
                resultUrl = "Sip1204";
                break;
            case 1205:
                resultUrl = "Sip1205";
                break;
            case 1206:
                resultUrl = "Sip1206";
                break;
            case 1207:
                resultUrl = "Sip1207";
                break;
            case 1210:
                resultUrl = "Sip1210";
                break;
            case 1213:
                resultUrl = "Sip1213";
                break;
            case 1214:
                resultUrl = "Sip1214";
                break;
            case 1215:
                resultUrl = "Sip1215";
                break;
            case 914://914是为了兼容老版本
                resultUrl = "Sip914";
                break;
            case 1212:
                resultUrl = "Sip1212";
                break;

            /**
             *B1系列的IPCAM SIP1016技术方案
             *sip1016                 1016                SIP1016
             *                        908                 SIP1016         //908是为了兼容老版本
             */
            case 908:
                resultUrl = "Sip908";
                break;
            case 1016:
                resultUrl = "Sip1016";
                break;

            /**
             *新增的1601系列设备 SIP1601技术方案
             *sip1601                 1601                SIP1601
             *sip1602                 1602                SIP1601
             *sip1603                 1603                SIP1601
             *sip1604                 1604                SIP1601
             *sip1605                 1605                SIP1601
             *sip1606w                16060000            SIP1601
             *sip1606w                1606                SIP1601
             */
            case 1601:
                resultUrl = "Sip1601";
                break;
            case 1602:
                resultUrl = "Sip1602";
                break;
            case 1603:
                resultUrl = "Sip1603";
                break;
            case 1604:
                resultUrl = "Sip1604";
                break;
            case 1605:
                resultUrl = "Sip1605";
                break;
            case 16060000:
                resultUrl = "Sip1606W";
                break;
            case 1606:
                resultUrl = "Sip1606";
                break;
            case 913:
                resultUrl = "Sip913";
                break;

            /**
             *新增的1211系列设备 SIP1211技术方案
             *sip1211                 1211                SIP1211
             */
            case 1211:
                resultUrl = "Sip1211";
                break;

            /**
             *新增的1303系列设备 SIP1303技术方案
             *SIP1303                 1303                SIP1303
             */
            case 1305:
                resultUrl = "Sip1305";
                break;
            case 1303:
                resultUrl = "Sip1303";
                break;

            case 0:
                resultUrl = context.getResources().getString(R.string.see_world_common_kaicong_zhiyun);
                break;
        }

        return resultUrl;
    }

}
