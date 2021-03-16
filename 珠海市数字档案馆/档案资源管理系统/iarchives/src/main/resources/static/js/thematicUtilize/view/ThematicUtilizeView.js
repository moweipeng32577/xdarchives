/**
 * Created by yl on 2017/10/27.
 */
Ext.define('ThematicUtilize.view.ThematicUtilizeView', {
    extend: 'Comps.view.BasicDataView',
    xtype: 'thematicUtilizeView',
    header:false,
    hasCloseButton:false,
    searchstore:[{item: "title", name: "专题名称"}],
    tbar: [{
        xtype: 'button',
        text: '下载',
        iconCls:'fa fa-download',
        itemId:'download'
    },{
        xtype: 'button',
        text: '查看',
        iconCls:'fa fa-eye',
        itemId:'look'
    },{
        xtype: 'button',
        text: '查看文件',
        iconCls:'fa fa-eye',
        itemId:'lookEle'
    }],
    datastore:'ThematicUtilizeGridStore'
});