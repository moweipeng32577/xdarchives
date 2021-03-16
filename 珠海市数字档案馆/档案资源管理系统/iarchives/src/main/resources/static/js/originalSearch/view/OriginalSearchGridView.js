/**
 * Created by RonJiang on 2017/11/2 0002.
 */
Ext.define('OriginalSearch.view.OriginalSearchGridView',{
    extend:'Comps.view.BasicGridView',
    xtype:'originalSearchGridView',
    title: '当前位置：原文检索',
    itemId:'originalSearchGridViewId',
    store: 'OriginalSearchGridStore',
    hasSearchBar:false,
    //hasCheckColumn:false,
    tbar: [{
        itemId:'exportId',
        xtype: 'button',
        iconCls:'fa fa-share-square-o',
        text: '导出Excel'
    }],
    columns: [
        {text: '文件编号', dataIndex: 'eleid', flex: 0, hidden:true},
        {text: '文件名称', dataIndex: 'filename', flex: 3, menuDisabled: true, align: 'center'},//文件名称 实体属性：filename
        {text: '文件类型', dataIndex: 'filetype', flex: 1, menuDisabled: true, align: 'center'},//文件类型 实体属性：filetype
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
                        var originalSearchShowWin = Ext.create("Ext.window.Window",{
                            width:'100%',
                            height:'100%',
                            header:false,
                            draggable : false,//禁止拖动
                            resizable : false,//禁止缩放
                            modal:true,
                            closeToolText:'关闭',
                            layout:'fit',
                            items:[{
                                xtype: 'EntryFormView'
                            }]
                        });
                        var record = view.getWidgetRecord();
                        var entryid = record.get('entryid');
                        var etips = originalSearchShowWin.down('[itemId=etips]');
                        etips.show();
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
                                var form = originalSearchShowWin.down('dynamicform');
                                form.nodeid = responseText.data.nodeid;
                                form.removeAll();//移除form中的所有表单控件
                                var formField = form.getFormField();//根据节点id查询表单字段
                                if(formField.length==0){
                                    XD.msg('请检查模板设置信息是否正确');
                                    return;
                                }else{
                                    form.initField(formField,'hide');//重新动态添加表单控件
                                    //字段编号，用于特殊的自定义字段(范围型日期)
                                    var prebtn = form.down('[itemId=preBtn]');
                                    var nextbtn = form.down('[itemId=nextBtn]');
                                    prebtn.hide();
                                    nextbtn.hide();
                                    var fieldCode = form.getRangeDateForCode();
                                    var entry = responseText.data.entry;
                                    form.loadRecord({getData:function(){return entry;}});
                                    if(fieldCode!=null){
                                        //动态解析数据库日期范围数据并加载至两个datefield中
                                        form.initDaterangeContent(entry);
                                    }
                                    var fields = form.getForm().getFields().items;
                                    Ext.each(fields,function (item) {
                                        item.setReadOnly(true);
                                    });
                                    //初始化原文数据
                                    var eleview = originalSearchShowWin.down('electronic');
                                    eleview.initData(entryid);
                                    var solidview = originalSearchShowWin.down('solid');
                                    solidview.initData(entryid);
                                    // var longview = originalSearchShowWin.down('long');
                                    // longview.initData(entryid);
                                    // form.formStateChange('look');
                                    form.fileLabelStateChange(eleview,'look');
                                    form.fileLabelStateChange(solidview,'look');
                                    // form.fileLabelStateChange(longview,'look');

                                    originalSearchShowWin.show();
                                    window.originalSearchShowWins = originalSearchShowWin;
                                    Ext.on('resize',function(a,b){
                                        window.originalSearchShowWins.setPosition(0, 0);
                                        window.originalSearchShowWins.fitContainer();
                                    });
                                }
                            }
                        });
                    }
                }
            }
        }
    ]
});