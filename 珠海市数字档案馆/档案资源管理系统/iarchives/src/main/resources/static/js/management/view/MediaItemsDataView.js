/**
 * Created by Leo on 2019/11/19 0019.
 */
var functionBtnMedia = [];
var needMap = {
    著录:"著录",
    修改:"修改",
    删除:"删除",
    查看:"查看",
    // 查看整卷文件:"查看整卷文件",
    // 查看案卷:"查看案卷",
    归档:"归档",
    // 拆件:"拆件",
    // 插件:"插件",
    移交送审:"移交送审",
    // 档号对齐:"档号对齐",
    过程追溯:"过程追溯",
    退回采集:"退回采集",
    查看退回:"查看退回"
};
if(functionButton != null) {
    for(var i = 0; i < functionButton.length; i= i + 2){
        var btnTemp = functionButton[i];
        if(btnTemp.text != null && needMap[btnTemp.text] != null){
            functionBtnMedia.push(btnTemp);
            if(i != functionButton.length - 1)
                functionBtnMedia.push("-");
        }
    }
}
Ext.define('Management.view.MediaItemsDataView', {
    extend: 'Management.view.BasicMediaDataView',
    xtype: 'mediaItemsDataView',
    hasPageBar:true,            //分页栏
    hasSearchBar:true,          //搜索栏
    hasCloseButton:false,        //关闭按钮
    hasCancelButton:false,
    autoHeight:true,
    draggable: false,//禁止拖动
    resizable: false,//禁止缩放
    width: '100%',
    height: '100%',
    searchstore: {
        proxy: {
            type: 'ajax',
            url: '/template/queryName',
            extraParams: {nodeid: 0},
            actionMethods:{read:'POST'},
            reader: {
                type: 'json',
                rootProperty: 'content',
                totalProperty: 'totalElements'
            }
        }
    },
    tbar: {
        overflowHandler: 'scroller',
        itemId:"functionTbar",
        items: functionBtnMedia
    },
    datastore: 'MediaItemsDtStore'
});
