/**
 * Created by zengdw on 2018/05/09 0001.
 */

Ext.define('Qrcode.view.QrcodeView', {
    extend: 'Ext.panel.Panel',
    xtype: 'qrcodeView',
    layout:'border',
    items:[{
        region:'north',
        columnWidth:.3,
        xtype:'textfield',
        labelAlign:'right',
        //labelWidth:100,
        emptyText:'扫描信息：',
        blankText : '扫描信息：',
        itemId:'qrcodeTxt',
        name:'qrcodeTxt',
        // value:'w1-2017-永久-0001',
        //value:'2017-工业-30年-0001',
        margin:'10 10 5 5'

    },{
        region:'center',
        itemid:'managementform',
        xtype:'managementformView',
    }]

});