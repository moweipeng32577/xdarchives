/**
 * Created by Leo on 2019/04/25.
 */
Ext.define('FileChecker.store.FileCheckerStore',{
    extend:'Ext.data.Store',
    model:'FileChecker.model.FileCheckerModel',
    pageSize: XD.pageSize,

    checkfileMap:{},
    tempPage:0,
    tempPageSize:0,
    fatherGrid:{},
    lastCheckTime:"",
    //获取自己的文件数据
    proxy: {
        type: 'ajax',
        url: '/electronic/getElectronics',
        extraParams: {},
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    },
    listeners:{
        load: function (store, operation, eOpts) {
           if(store.tempPageSize != 0 ){
               var tempMap = this.getDataMap(store.currentPage,store.pageSize);
               if(tempMap != undefined && tempMap != null){
                   store.checkfileMap = JSON.parse(tempMap);

               }
           }
            this.setMark(store);

        }
    },
    setMark:function(store){
        for(var i = 0, len = store.data.length; i < len; i++){
            var electid = store.getAt(i).get('id');
            var val = store.checkfileMap[electid.toString()];
            if(val == undefined || val == null || val == ""){
                continue;
            }
            //空值
            if(val == 0) {
                store.getModel()
                store.getAt(i).set('resultText','文件不存在');
                store.getAt(i).set('lastCheckTime',store.lastCheckTime);
                // store.fatherGrid.getView().getRow(i).style.background = "#FFC1C1";
                // delete store.checkfileMap[electid.toString()];
            }
            //更新
            else if(val == 1){
                store.getAt(i).set('resultText','文件被更新');
                store.getAt(i).set('lastCheckTime',store.lastCheckTime);
                // store.fatherGrid.getView().getRow(i).style.background = "#BFEFFF";
                // delete store.checkfileMap[electid.toString()];
            }
            if(JSON.stringify(store.checkfileMap)=="{}")
                break;
        }
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
});
