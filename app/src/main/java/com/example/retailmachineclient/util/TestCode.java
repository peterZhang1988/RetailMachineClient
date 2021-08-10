package com.example.retailmachineclient.util;

import com.example.retailmachineclient.R;
import com.example.retailmachineclient.base.BaseApplication;

class TestCode {
    //    public void WrapData() {
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                compareAfterList.clear();
//                GoodInfoModel copy = null;
//                boolean isChanged = false;
//                for (GoodInfoModel oldInfoModel : compareBeforeList) {
//                    in:
//                    for (GoodInfoModel newInfoModel : requestNumDataList) {
//                        copy = new GoodInfoModel();
//                        if (oldInfoModel.getContainerNum() == newInfoModel.getContainerNum()) {
//                            if (oldInfoModel.getGoodsInventory() != newInfoModel.getGoodsInventory()
//                                    || oldInfoModel.getGoodsMaxInventory() != newInfoModel.getGoodsMaxInventory()) {
//                                //modify
//                                isChanged = true;
//                            }
//                            Utils.wrapGood(copy, oldInfoModel);
//                            copy.setGoodsInventory(newInfoModel.getGoodsInventory());
//                            copy.setGoodsMaxInventory(newInfoModel.getGoodsMaxInventory());
//                            if (isChanged) {
//                                GoodInfoModel dataBaseModel = LitePal.where("ContainerNum == ?", "" + newInfoModel.getContainerNum()).findFirst(GoodInfoModel.class);
//                                if (dataBaseModel != null) {
//                                    dataBaseModel.setGoodsInventory(newInfoModel.getGoodsInventory());
//                                    dataBaseModel.setGoodsMaxInventory(newInfoModel.getGoodsMaxInventory());
//                                    boolean isSave = dataBaseModel.save();
//                                    Logger.e("数据 更新数据库 isSave=" + isSave);
//                                }
//                            }
//                            compareAfterList.add(copy);
//                            break in;
//                        }
//                    }
//                }
//                if (isChanged) {
//                    Logger.e("数据 有差异！");
//                } else {
//                    Logger.e("数据 没有差异！");
//                }
//                //将数据排序
//                compareBeforeList.clear();
//                for (GoodInfoModel goodsModel : compareAfterList) {
//                    if (goodsModel.getGoodsInventory() != 0) {
//                        compareBeforeList.add(goodsModel);
//                    }
//                }
//                for (GoodInfoModel goodsModel : compareAfterList) {
//                    if (goodsModel.getGoodsInventory() == 0) {
//                        compareBeforeList.add(goodsModel);
//                    }
//                }
//
//                requestDataList.clear();
//                requestDataList.addAll(compareBeforeList);
//                EventBus.getDefault().post(new MessageEvent(MessageEvent.EventType_QUERY_GOODS_NUM_OPERATE, isChanged));
//            }
//        };
//        ThreadPoolManager.getInstance().executeRunable(runnable);
//    }

//    else if (current.equals(LanguageType.FRENCH.getLanguage())) {
//        btChinese.setBackground(null);
//        btEnglish.setBackground(null);
//        btFrench.setBackground(BaseApplication.getContext().getResources().getDrawable(R.mipmap.bt_bg));
//        btChinese.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
//        btEnglish.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.default_txt_lan));
//        btFrench.setTextColor(BaseApplication.getContext().getResources().getColor(R.color.textSelectColor));
//    }

    //初始化数据
//        for (int i = 0; i < 30; i++) {
//            GoodInfoModel goodModel = new GoodInfoModel();
//            goodModel.setGoodsName1("可口可乐" + i);
//            goodModel.setGoodsInventory(6);
//            goodModel.setPrice(3);
//            goodModel.setGoodsDescription1("可乐除了饮用，还可以用来刷马桶，这是因为马桶中的污垢主要是尿碱混合其他污染物质沉积"+
//                    "造成的，其主要成分为磷酸钙，不溶于水因此难以清洗。可乐中添加了磷酸二氧化碳和柠檬酸等，可以与磷酸钙反应生成溶于水的物质。" + i);
//
//            goodModel.setContainerFloor((i)/6);
//            String s = ""+i/6+""+i%6;
//
//            int b = Integer.parseInt(s.replaceAll("^0[x|X]", ""), 16);
//            goodModel.setContainerNum(Integer.valueOf(s).intValue());
//            dataList.add(goodModel);
//        }

    //    public TxErrorModel getOneGood(int line, int row) {//检验错误码 errorCode
//        Logger.e("出货流程 run getOneGood");
//
//        TxErrorModel txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS_IS_POSITION, System.currentTimeMillis(), 10000, 0, 7);
//        boolean isReset = false;
//        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
//            if (txErrorModel.getZ2() == 7) {
//                isReset = true;
//            }
//        } else {
//            if (txErrorModel != null) {
//                txErrorModel.setSuccess(false);
//            }
//            return txErrorModel;
//        }
//
//        boolean resetResult = true;
//        if (!isReset) {
//            //没有复位
//            Logger.e("出货流程 启动升降机 没有复位 ");
//            txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
//            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
//                int tempLine = (line + 2) % 5;
//                liftMoveNew((byte) tempLine);
//                txErrorModel = processByStatus(TYPE_OPERATE_PALLET_TO_7, System.currentTimeMillis(), 10000, 3, 7);
//                if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
//                    Logger.e("出货流程 启动升降机 一直复位不成功");
//                    //一直复位不成功
//                    txErrorModel.setSuccess(false);
//                    txErrorModel.setErrorCode(TYPE_ERROR_RESET_TO_7_FAIL);
//                    return txErrorModel;
//                }
//            }
//        }
//        //已经复位复位完成
//        //升降移动指定位置 连续多次
//        txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
//        if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
//            Logger.e("出货流程 启动升降机到指定楼层 失败");
//            //做一次兜底动作
//            int tempLine = (line + 2) % 5;
//            liftMoveNew((byte) tempLine);
//
//            txErrorModel = processByStatus(TYPE_OPERATE_PALLET, System.currentTimeMillis(), 10000, 3, line);
//            if (txErrorModel != null && !txErrorModel.isTaskSuccess()) {
//                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_FAIL);
//            }
//
//        }
//        Logger.e("出货流程 启动升降机 正常启动 升降机是否到达指定位置");
//        //升降机是否到达指定位置
//
//        txErrorModel = processByStatus(TYPE_QUERY_PALLET_STATUS, System.currentTimeMillis(), 10000, 0, line);
//        if (txErrorModel != null && txErrorModel.isTaskSuccess()) {
//            int compare = 0;
//            if (txErrorModel.getZ2() < 5) {
//                compare = 4 - txErrorModel.getZ2();
//            } else {
//                compare = txErrorModel.getZ2();
//            }
//            if (compare == line) {
//                Logger.e("出货流程 启动升降机 移动到指定位置");
//            } else {
//                Logger.e("出货流程 启动升降机 未移动到指定位置");
//                return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
//            }
//        } else {
//            return new TxErrorModel(false, false, TYPE_ERROR_MOVE_TARGET_CHECK_FAIL);
//        }
//        //查询升降机当前位置
//        Logger.e("出货流程 电机启动前准备检查是否需要检查状态");
//        //启动电机
//        int value = startMachine(row);
//        if (value != 0) {
//            //电机启动异常
//            return new TxErrorModel(false, false, TYPE_ERROR_START_MACHINE);
//        }
//        Logger.e("出货流程 电机启动后 查询执行旋转结果");
//        txErrorModel = processByStatus(TYPE_QUERY_MACHINE_STATUS_IS_POSITION, System.currentTimeMillis(), 5000, 0, 0);
//        if (!txErrorModel.isTaskSuccess()) {
//            //电机
//            Logger.e("电出货流程 机启动后 执行旋转异常");
//            return new TxErrorModel(false, false, TYPE_ERROR_MACHINE_RUN);
//        }
//        return new TxErrorModel(true, true, 0);
//    }

}
