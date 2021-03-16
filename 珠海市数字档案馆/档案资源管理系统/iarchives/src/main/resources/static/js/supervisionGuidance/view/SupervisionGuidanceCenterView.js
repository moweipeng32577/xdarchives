/**
 * Created by Administrator on 2020/9/28.
 */


Ext.define('SupervisionGuidance.view.SupervisionGuidanceCenterView', {
    extend: 'Ext.panel.Panel',
    xtype: 'supervisionGuidanceCenterView',
    layout: 'fit',
    // bodyBorder: false,
    autoScroll: true,
    items: [{
        // region:'north',
        height:'100%',
        width:'100%',
        xtype: 'supervisionGuidanceCenterTopView'
    },
    //     {
    //     // region: 'center',
    //     xtype: 'fieldset',
    //     title:"保管条件",
    //     margin: '0 10 0 10',
    //     height:200,
    //     layout: 'fit',
    //     items:[{
    //         xtype:'form',
    //         layout:'column',
    //         width:'100%',
    //         itemId:'fieldsetFormId',
    //         autoScroll: true,
    //         items:[{
    //             xtype: 'textfield',
    //             fieldLabel: '',
    //             name: 'id',
    //             hidden: true
    //         }, {
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'addressId',
    //             fieldLabel: '档案库房地址',
    //             margin: '5 0 0 0',
    //             name: 'address',
    //             labelWidth: 140
    //         }, {
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         }, {
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'areaId',
    //             fieldLabel: '档案库房面积（m2）',
    //             margin: '5 0 0 0',
    //             name: 'area',
    //             labelWidth: 140
    //         },{
    //             columnWidth: .05,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'airnumId',
    //             fieldLabel: '配备空调机数量（台）',
    //             margin: '5 0 0 0',
    //             name: 'airnum',
    //             labelWidth: 140
    //         }, {
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'dehumidifiernumId',
    //             fieldLabel: '配备除湿机数量（台）',
    //             margin: '5 0 0 0',
    //             name: 'dehumidifiernum',
    //             labelWidth: 140
    //         },{
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'filingnumId',
    //             fieldLabel: '档案柜（套）',
    //             margin: '5 0 0 0',
    //             name: 'filingnum',
    //             labelWidth: 140
    //         }, {
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         }, {
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'firenumId',
    //             fieldLabel: '配备灭火器数量（个）',
    //             margin: '5 0 0 0',
    //             name: 'firenum',
    //             labelWidth: 140
    //         },{
    //             columnWidth: .05,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'denseframenumId',
    //             fieldLabel: '密集架（列）',
    //             margin: '5 0 0 0',
    //             name: 'denseframenum',
    //             labelWidth: 140
    //         }, {
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'computernumId',
    //             fieldLabel: '计算机（台）',
    //             margin: '5 0 0 0',
    //             name: 'computernum',
    //             labelWidth: 140
    //         },{
    //             columnWidth: .23,
    //             xtype: 'textfield',
    //             itemId:'othernumId',
    //             fieldLabel: '其他设备（台）',
    //             margin: '5 0 0 0',
    //             name: 'othernum',
    //             labelWidth: 140
    //         }, {
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         }, {
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'issecurityId',
    //             margin: '5 0 0 0',
    //             name:'issecurity',
    //             inputValue : '1',
    //             boxLabel: '是否有防盗措施'
    //         },{
    //             columnWidth: .05,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'isonlystoomId',
    //             margin: '5 0 0 0',
    //             name:'isonlystoom',
    //             inputValue : '1',
    //             boxLabel: '是否设置独立的档案库房'
    //         },{
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'isonlypreviewId',
    //             margin: '5 0 0 0',
    //             name:'isonlypreview',
    //             inputValue : '1',
    //             boxLabel: '是否设置独立的预览室'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'islightmeasureId',
    //             margin: '5 0 0 0',
    //             name:'islightmeasure',
    //             inputValue : '1',
    //             boxLabel: '是否有防光措施'
    //         },{
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'isbiologicalmeasureId',
    //             margin: '5 0 0 0',
    //             name:'isbiologicalmeasure',
    //             inputValue : '1',
    //             boxLabel: '是否有防有毒生物措施'
    //         }, {
    //             columnWidth: .05,
    //             xtype: 'displayfield'
    //         },{
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'ismetalcabinetId',
    //             margin: '5 0 0 0',
    //             name:'ismetalcabinet',
    //             inputValue : '1',
    //             boxLabel: '是否有存放全部档案的金属柜架'
    //         }, {
    //             columnWidth: .01,
    //             xtype: 'displayfield'
    //         }, {
    //             columnWidth: .23,
    //             xtype: 'checkbox',
    //             itemId:'ischeckrecordId',
    //             margin: '5 0 0 0',
    //             name:'ischeckrecord',
    //             inputValue : '1',
    //             boxLabel: '是否定时对库房设备检查并记录'
    //         }]
    //     }]
    // }
    ],
    buttonAlign: 'center',
    buttons:[{
        text: '保存',
        itemId:'saveAllId'
    }]
});
