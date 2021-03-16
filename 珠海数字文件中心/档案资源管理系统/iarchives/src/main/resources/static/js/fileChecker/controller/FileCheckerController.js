/**
 * Created by Leo 2019/04/25.
 */
Ext.define('FileChecker.controller.FileCheckerController', {
    extend: 'Ext.app.Controller',

    views: ['FileCheckerView'],
    stores: ['FileCheckerStore'],
    models: ['FileCheckerModel'],
    init: function () {
        this.control({
            'fileCheckerView':{
                afterrender:function (grid) {
                    var _this = this;
                    var bar = grid.getDockedItems('toolbar[dock="top"]');
                    var p = Ext.create('Ext.ProgressBar', {
                        itemid:'proBar',
                        width: 300,
                    });
                    bar[0].add('->');
                    bar[0].add(p);
                    p.hide();
                    var resultCheck = this.isChecking();
                    if(resultCheck == 1){
                        p.show();
                        this.checkFileTimer(grid,p);
                    }
                    else if(resultCheck == 2){
                        p.show();
                        _this.checkFileTimer(grid,p);
                        // setTimeout(1000);
                        // XD.confirm("最近3天内有进行过文件巡查，是否加载结果？",function(){
                        //
                        //
                        // });
                    }
                    grid.initGrid();
                }
            },
            'fileCheckerView button[itemId="checkfile"]' : {
                click : function (btn){
                    var _this = this;
                    Ext.Ajax.request({
                        url: '/electronic/checkfile',   //点击开始巡查
                        method: 'GET',
                        sync: true,
                        success: function (resp) {
                            var grid = btn.up('fileCheckerView');
                            var isChecking = resp.responseText;
                            var FileCheckProgressBar = grid.down('[itemid=proBar]');
                            FileCheckProgressBar.show();
                            if(isChecking != 1 )
                            {
                                XD.msg('开始巡查');
                                //加入进度条
                                grid.isChecking = 1;
                                //启动计时器
                                _this.checkFileTimer(grid, FileCheckProgressBar);
                            }
                            else if(isChecking == 1)
                            {
                                XD.msg('正在巡查中，请耐心等待');
                                _this.checkFileTimer(grid, FileCheckProgressBar);

                            }
                        },
                        failure: function() {
                            XD.msg('巡查失败');
                        }
                    })
                }
            },
        });
    },
    //定时器
    checkFileTimer:function(grid, FileCheckProgressBar){
        var _this = this;
        var x = setInterval(function(){
            Ext.Ajax.request({
                method : 'GET' ,
                url:  '/electronic/checkfileResult', //请求路径
                timeout:100,
                async:false,
                success: function (repon) {
                    grid.getStore().fatherGrid = grid;
                    var searchResultMap = repon.responseText;
                    var tempMap = JSON.parse(searchResultMap);
                    // var dataMap = tempMap["resultMap"];          // 巡查结果集

                    var dateTime = tempMap["lastCheckDate"];    //  最后巡查日期
                    FileCheckProgressBar.updateProgress(tempMap["scheduleRate"],"正在巡查中:" + parseFloat(tempMap["scheduleRate"]*100).toFixed(2) + "%",true);
                    if(grid.getStore().checkfileMap == null || grid.getStore().tempPage != grid.getStore().currentPage || grid.getStore().tempPageSize != grid.getStore().pageSize){
                        grid.getStore().tempPage = grid.getStore().currentPage;
                        grid.getStore().tempPageSize = grid.getStore().pageSize;
                        // grid.getStore().resultCheck = 2;
                        var dataMap = _this.getDataMap(grid.getStore().currentPage,grid.getStore().pageSize);
                        if (dataMap != undefined && dataMap != null)
                            grid.getStore().checkfileMap = JSON.parse(dataMap);    //发送获取当前页 需要渲染的结果集 的请求
                    }
                    grid.getStore().lastCheckTime = dateTime;
                    grid.getStore().setMark(grid.getStore());   //设置标识
                    if(tempMap["scheduleRate"] >= 1){
                        FileCheckProgressBar.updateProgress(tempMap["scheduleRate"],"巡查完毕:100%",true)
                        window.clearInterval(x);
                    }
                }
            });
        }, 1000);
        x;
    },
    //判断是否在巡查中
    isChecking: function(){
        var result = 0;
        Ext.Ajax.request({
            method : 'GET' ,
            url:  '/electronic/isChecking', //请求路径
            async:false,
            timeout:1000,
            success: function (repon) {
                result = repon.responseText;
            }
        });
        return result;
    },
    //获取当前页需要渲染的结果集
    getDataMap:function(currentPage,PageSize){
        Ext.Ajax.request({
            method : 'POST' ,
            url:  '/electronic/getDataMap', //请求路径
            async:false,
            timeout:100,
            params:{
                currentPage:currentPage,
                PageSize:PageSize
            },
            success: function (repon) {
                result = repon.responseText;
            }
        });
        return result;
    }
    // ,
    // setColor:function(grid){
    //     grid.getStore().load({
    //         callback:function(records, operation, success){
    //             for(var i = 0, len = grid.getStore().data.length; i < len; i++){
    //                 var electid = grid.getStore().getAt(i).get('id');
    //                 var val = grid.getStore().checkfileMap[electid.toString()];
    //                 if(val == undefined || val == null || val == ""){
    //                     continue;
    //                 }
    //                 //空值
    //                 if(val == 0) {
    //                     grid.getView().getRow(i).style.background = "#FFC1C1";
    //                     // delete store.checkfileMap[electid.toString()];
    //                 }
    //                 //更新
    //                 else if(val == 1){
    //                     grid.getView().getRow(i).style.background = "#BFEFFF";
    //                     // delete store.checkfileMap[electid.toString()];
    //                 }
    //                 if(JSON.stringify(store.checkfileMap)=="{}")
    //                     break;
    //             }
    //         }
    //     })
    // }

});