var archiveTypeStore = Ext.create("Ext.data.Store", {
    fields: ["Name", "Value"],
    data: [
        { Name: "案卷", Value: '案卷' },
        { Name: "按件", Value: '按件'}
    ]
});
Ext.define('MetadataAdmin.view.MetadataFormView', {
    extend: 'Ext.window.Window',
    xtype: 'MetadataFormView',
    itemId:'MetadataFormViewId',
    title: '修改元数据',
    frame: true,
    resizable: true,
    closeToolText:'关闭',
    width: 610,
    minWidth: 610,
    height: '80%',
    modal:true,
    items: [
        {
            xtype: 'container',
            layout:'form',
            height:'100%',
            // margin: '15',
            style: 'overflow:auto;',
            items: [
                {
                    xtype: 'form',
                    layout:'form',
                    height:'100%',
                    items:[
                        {xtype: 'textfield', fieldLabel: 'id',name:'id',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'batchcode',name:'batchcode',hidden:true},
                        {xtype: 'textfield', fieldLabel: 'mediaid',name:'mediaid',hidden:true},
                        {xtype: 'textfield', fieldLabel: '档号',name:'archivecode'},
                        {xtype: 'textfield', fieldLabel: '电子档案名称',name:'filename'},
                        {xtype: 'textfield', fieldLabel: '扫描件页号',name:'scanpagecode'},
                        {xtype: 'textfield', fieldLabel: '稿本代码',name:'qbcode'},
                        {xtype: 'textfield', fieldLabel: '存储路径',name:'filepath'},
                        {xtype: 'textfield', fieldLabel: '数字化时间',name:'digitaltime'},
                        {xtype: 'textfield', fieldLabel: '数字化对象描述',name:'digitalobjdescribe'},
                        {xtype: 'textfield', fieldLabel: '数字化授权描述',name:'describeaccreditdescribe'},
                        {xtype: 'textfield', fieldLabel: '格式名称',name:'formatname'},
                        {xtype: 'textfield', fieldLabel: '格式版本',name:'formatversion'},
                        {xtype: 'textfield', fieldLabel: '色彩空间',name:'colorspace'},
                        {xtype: 'textfield', fieldLabel: '压缩方案',name:'reduceplan'},
                        {xtype: 'textfield', fieldLabel: '压缩比',name:'reduceratio'},
                        {xtype: 'textfield', fieldLabel: '水平分辨率',name:'levelresolution'},
                        {xtype: 'textfield', fieldLabel: '垂直分辨率',name:'verticalresolution'},
                        {xtype: 'textfield', fieldLabel: '设备类型',name:'equipmenttype'},
                        {xtype: 'textfield', fieldLabel: '设备制造商',name:'equipmentmanufacturer'},
                        {xtype: 'textfield', fieldLabel: '设备型号',name:'equipmentmodel'},
                        {xtype: 'textfield', fieldLabel: '设备感光器',name:'equipmentsensitization'},
                        {xtype: 'textfield', fieldLabel: '数字化软件名称',name:'digitalsoftname'},
                        {xtype: 'textfield', fieldLabel: '数字化软件版本',name:'digitalsoftversion'},
                        {xtype: 'textfield', fieldLabel: '数字化软件生产商',name:'inspector'},
                        {xtype: 'textfield', fieldLabel: '阅读所需软硬件条件',name:'digitalsoftvendor'},
                        {xtype: 'textfield', fieldLabel: '数字化成果移交接收信息',name:'readsoftcondition'},
                        {xtype: 'textfield', fieldLabel: '图像宽度',name:'picturewidth'},
                        {xtype: 'textfield', fieldLabel: '图像高度',name:'pictureheight'},
                        {xtype: 'textfield', fieldLabel: '位深度',name:'bitdepth'},
                        {xtype: 'textfield', fieldLabel: '版权',name:'copyright'},
                        {xtype: 'textfield', fieldLabel: '文件大小',name:'filesize'},
                        {xtype: 'textfield', fieldLabel: 'MD5码',name:'f44',readOnly:true}
                    ]
                }
            ]
        }
    ],

    buttons: [
        { text: '提交',itemId:'submit'},
        { text: '关闭',itemId:'close'}
    ]
});