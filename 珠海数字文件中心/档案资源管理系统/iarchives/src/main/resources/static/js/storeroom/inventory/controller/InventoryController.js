/**
 * Created by Rong on 2018/4/27.
 */
Ext.define('Inventory.controller.InventoryController',{
    extend: 'Ext.app.Controller',

    views: ['InventoryView','InventoryTabView','InventoryShowView','InventoryAddView','InventoryResultShowView'],
    stores:['CityStore','UnitStore','RoomStore','ZoneStore','ColStore','InventoryStore','InventoryResultStore'],
    model:['CityModel','UnitModel','RoomModel','ZoneModel','ColModel','InventoryModel','InventoryResultModel'],

    init:function(){
        this.control({
            'inventoryAddView button[itemId=cancel]':{
                click:function(btn){
                    btn.findParentByType('inventoryAddView').close();
                }
            },'inventoryShowView button[itemId=staErr]'  :{
                click:function (btn) {
                    var ztStr='3'
                    this.stateErrHandler(btn,ztStr);
                }
            },'inventoryShowView button[itemId=shelErr]'  :{
                click:function (btn) {
                    var ztStr='4'
                    this.stateErrHandler(btn,ztStr);
                }
            },'inventoryShowView button[itemId=lessErr]'  :{
                click:function (btn) {
                    var ztStr='2'
                    this.stateErrHandler(btn,ztStr);
                }
            },'inventoryShowView button[itemId=moreErr]'  :{
                click:function (btn) {
                    var ztStr='1'
                    this.stateErrHandler(btn,ztStr);
                }
            },'inventoryResultShowView button[itemId=modi]':{
                click:this.modiHandler
            },'inventoryResultShowView button[itemId=shelmove]':{
                click:this.shelmoveHandler
            },'inventoryTabView': {
                tabchange: function (view) {
                    if (view.activeTab.title == '结果分析') {
                        var gridcard=view.down('[itemId=inventorygrid]');
                        gridcard.getStore().reload();
                        //清空之前的选择项
                        gridcard.getSelectionModel().clearSelections();
                        gridcard.getView().refresh();
                    }
                }
            },
            'inventoryAddView fileuploadfield[itemId=importFileNameID]'  :{
                change: function (field, newValue, oldValue, eOpts) {
                    if ('' != newValue) {
                        // 文件类型判断
                        // console.log('newValue:'+newValue);
                        var value=newValue.substr(12);
                        // console.log('value:'+value);
                        var arrType = newValue.split('.');
                        var docType = arrType[arrType.length - 1].toLowerCase();
                        if ('txt' != docType) {
                            Ext.MessageBox.alert("提示", "选择文件格式不正确，请选择.txt格式的文件上传", callBack);
                            function callBack() {
                                field.reset();
                            }
                        }else{
                            field.setRawValue(value);
                        }
                    }
                }
            },
            'inventoryAddView button[itemId=save]'  :{
                click: function (view, e, eOpts) {

                    var importWindow = view.findParentByType('inventoryAddView');
                    var form = importWindow.down('[itemId=fileForm]');
                    var importFileName =getChildView(form,new Array('importFileNameID'));
                    var StroRange =importWindow.down('[itemId=shid]');
                    var StroRangeMsg =importWindow.down('[itemId=shidMsg]');
                    var des=importWindow.down('[itemId=descript]');

                    var city=importWindow.down('[itemId=city]');
                    var unit=importWindow.down('[itemId=unit]');
                    var room=importWindow.down('[itemId=room]');
                    var zone=importWindow.down('[itemId=zone]');
                    var col=importWindow.down('[itemId=col]');
                    //先判断数据不为空
                    if (city.getValue() == '' || city.getValue() == null ) {//没选城市
                        XD.msg('请先选定一个城市');
                        return;
                    }else if (unit.getValue() == '' || unit.getValue() == null ) {//没选单位
                        XD.msg('请先选定一个单位');
                        return;
                    }else if (room.getValue() == '' || room.getValue() == null ) {//没选库房
                        XD.msg('请先选定一个盘点库房');
                        return;
                    }else if(zone.getValue() == '' || zone.getValue() == null ){//按库房盘点
                        var shid='room:'+city.getValue()+","+unit.getValue()+","+room.getValue();
                        var shidMsg=city.getValue()+"-"+unit.getValue()+"-"+room.getValue();
                        StroRangeMsg.setValue(shidMsg);
                        StroRange.setValue(shid);
                    }else if(col.getValue() == '' || col.getValue() == null ){//按区盘点
                        var shid='zone:'+zone.getValue();
                        var shidMsg=city.getValue()+"-"+unit.getValue()+"-"+room.getValue()+"-"+zone.getRawValue();
                        StroRangeMsg.setValue(shidMsg);
                        StroRange.setValue(shid);
                    }else if(col.getValue().length>0){
                        var shidMsg=city.getValue()+"-"+unit.getValue()+"-"+room.getValue()+"-"+zone.getRawValue()+"-"+col.getValue()+"列";
                        StroRangeMsg.setValue(shidMsg);
                    }
                    if (form.isValid()) {
                        if(''== importFileName.getValue()){
                            XD.msg('请选择文件');
                        }else{
                            form.submit({
                                clientValidation: true,
                                url: '/inventory/importData',
                                method:'POST',
                                success:function(){
                                        XD.msg('盘点比对完毕');
                                },
                                failure:function() {
                                    XD.msg('比对中断');
                                }
                            });
                        }
                    }
                }
            }
        });
    },

    stateErrHandler:function(btn,ztStr){
        var grid = btn.up('inventoryShowView').down('[itemId=inventorygrid]');
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        if(selectCount>1){
            XD.msg('只能选一条盘点记录');
            //清空选项
            grid.selModel.clearSelections();
            grid.getStore().reload();
            return;
        }else if(selectCount<1){
            XD.msg('请先选择一条盘点记录');
            return;
        }

        //Ajax有数据的话切换到inventory_result表格窗口，进行数据操作
        var win = this.getView('InventoryResultShowView').create({
            title:'盘点异常清单',
            modal: true,
            //resizable: false,
            closeToolText:'关闭',
            width:'60%',
            height:'75%',
            //store.load({params:{name:'张三',age:18}})
            //store.loadData(json.datas);
            buttons:[{
                text:'关闭',
                handler:function(){
                    win.close();
                }
            }]
        });
        var resultGrid=win.down('[itemId=inventoryResultShowGrid]');
        var modBtn=win.down('[itemId=modi]');
        var movBtn=win.down('[itemId=shelmove]');
        //resultGrid.store.load({params: {checkid: str,resulttype:ztStr}});
        //win.show();

        //获取该条选中盘点记录的编号checkid，根据checkid去st_inventory_result中查找resultType为3的数据
        var str=record[0].get("checkid");
        Ext.Ajax.request({
            params: {checkid: str,resulttype:ztStr},
            url: '/inventory/result',
            method: 'post',
            sync: true,
            success: function (response) {
                var respText = Ext.JSON.decode(response.responseText);
                //没有数据的话弹框提醒
                if (respText.success == true) {
                    if(respText.msg=='0'){
                        XD.msg('没有相关数据');
                        return;
                    }
                    //加载store
                    resultGrid.store.load({params: {checkid: str,resulttype:ztStr}});
                    //Ext.getCmp('按钮id').show();显示.hide()隐藏按钮
                    if(ztStr=='3'){

                        movBtn.hide();
                    }else{
                        modBtn.hide();
                        movBtn.hide();
                    }
                    win.show();
                }
            },
            failure: function () {
                XD.msg('操作中断');
            }
        });


    },

    modiHandler:function(btn){
        var grid = btn.up('inventoryResultShowView').down('[itemId=inventoryResultShowGrid]');
        var record = grid.selModel.getSelection();
        var selectCount = record.length;
        if(selectCount<1){
            XD.msg('请先选择一条记录');
            //清空选项
            grid.selModel.clearSelections();
            grid.getStore().reload();
        }
        //现获取chipcode,再获取查storage来entry和shid
        var tmp = [];
        for(var i = 0; i < record.length; i++){
            tmp.push(record[i].get('chipcode'));
        }
        var chipcodes = tmp.join(',');
        var staStr=record[0].get('resulttype');
        //清空选项
        grid.selModel.clearSelections();
        if(staStr==3){//状态有误，出库状态的档案却在库
            Ext.Ajax.request({
                params: {chipcodes: chipcodes},
                url: '/inventory/changeSta',
                method: 'post',
                sync: true,
                success: function (response) {
                    var respText = Ext.JSON.decode(response.responseText);

                    if (respText.success == true) {
                        XD.msg(respText.msg);
                        //清空选项
                        grid.selModel.clearSelections();
                        grid.getStore().reload();
                    }
                },
                failure: function () {
                    XD.msg('操作中断');
                    grid.getStore().reload();
                }
            });
        }else if(staStr==1){//盘点多出
			
        }else if(staStr==2){//未盘点到
			
        }
    },
    shelmoveHandler:function(btn){
		
    }
});
function getChildView(parentView, itemIDs) {
    var view = parentView;
    var childView;
    for (var id in itemIDs) {
        childView = view.getComponent(itemIDs[id]);
        view = childView;
    }
    return childView;
}