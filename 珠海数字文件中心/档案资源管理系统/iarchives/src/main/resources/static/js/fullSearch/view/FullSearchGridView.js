/**
 * Created by tanly on 2017/11/17 0002.
 */
Ext.define('FullSearch.view.FullSearchGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'fullSearchGridView',
    title: '当前位置：全文检索',
    itemId:'fullSearchGridViewId',
    store: 'FullSearchGridStore',
    columns: [
        {text: '文件名称', dataIndex: 'filename', flex: 1.5, menuDisabled: true, align: 'center'},//文件名称 实体属性：filename
        {text: '文件内容', dataIndex: 'filetext', flex: 4, menuDisabled: true, align: 'center'},//文件类型 实体属性：filetype
        {
            xtype: 'widgetcolumn',
            flex: 0.5,
            menuDisabled: true,
            widget: {
                xtype: 'button',
                width:'100%',
                text: '查看',
                listeners:{
                    click:function (view) {
                        var fullSearchShowWin = Ext.create("Ext.window.Window",{
                            width:'100%',
                            height:'100%',
                            draggable : false,//禁止拖动
                            resizable : false,//禁止缩放
                            modal:true,
                            closeToolText:'关闭',
                            header: false,
                            layout:'fit',
                            items:[{
                                xtype: 'EntryFormView'
                            }]
                        });
                        var record = view.getWidgetRecord();
                        var entryid = record.get('entryid');
                        Ext.Ajax.request({
                            method:'GET',
                            async:false,
                            scope:this,
                            url:'/originalSearch/entry/'+entryid,
                            success:function(response){
                                var responseText = Ext.decode(response.responseText);
                                if(responseText.success==false){
                                    XD.msg(responseText.msg);
                                    return;
                                }
                                var form = fullSearchShowWin.down('dynamicform');
                                form.nodeid = responseText.data.nodeid;
                                form.removeAll();//移除form中的所有表单控件
                                var formField = form.getFormField();//根据节点id查询表单字段
                                if(formField.length==0){
                                    XD.msg('请检查模板设置信息是否正确');
                                    return;
                                }
                                form.initField(formField);//重新动态添加表单控件
                                //字段编号，用于特殊的自定义字段(范围型日期)
                                var fieldCode = form.getRangeDateForCode();
                                var entry = responseText.data.entry;
                                form.loadRecord({
                                    getData:function(){
                                        return entry;
                                    }
                                });
                                if(fieldCode!=null){
                                    //动态解析数据库日期范围数据并加载至两个datefield中
                                    form.initDaterangeContent(entry);
                                }
                                //初始化原文数据
                                var eleview = fullSearchShowWin.down('electronic');
                                var solidview = fullSearchShowWin.down('solid');
                                if(iflag=='ly'){
                                    eleview.setDisabled(true);
                                    solidview.isJy = true;
                                }else{
                                    eleview.setDisabled(false);
                                    eleview.initData(entryid);
                                    solidview.isJy = false;
                                }
                                solidview.initData(entryid);
                                // var longview = fullSearchShowWin.down('long');
                                // longview.initData(entryid);
                                // form.formStateChange('look');
                                form.fileLabelStateChange(eleview,'look');
                                form.fileLabelStateChange(solidview,'look');
                                // form.fileLabelStateChange(longview,'look');

                                fullSearchShowWin.show();
                                window.fullSearchShowWins = fullSearchShowWin;
                                Ext.on('resize',function(a,b){
                                    window.fullSearchShowWins.setPosition(0, 0);
                                    window.fullSearchShowWins.fitContainer();
                                });
                            }
                        });
                    }
                }
            }
        }
    ],
    plugins: [{
        ptype: 'rowexpander',
        rowBodyTpl : new Ext.XTemplate(
            '<p align="left"><b>文件名称:</b> {filename}</p>',
            '<p align="left"><b>文件内容:</b> {filetext}</p>'
         )
    }],
    hasSearchBar:false,
    hasRownumber:false
});