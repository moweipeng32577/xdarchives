Ext.define('MetadataAdmin.view.MetadataAdminView',{
    extend: 'Comps.view.BasicGridView',
    xtype:'MetadataAdminView',
    itemId:'MetadataAdminViewID',
    bodyBorder: false,
    store: 'MetadataAdminGridStore',
    hasCloseButton:false,
    head:false,
    searchstore:[
        {item: "archivecode", name: "档号"},
        {item: "filename", name: "文件名"},
        {item: "scanpagecode", name: "扫描件页号"},
        ],
    tbar: [
        // {
        //     itemId:'add',
        //     xtype: 'button',
        //     iconCls:'fa fa-plus-circle',
        //     text: '增加'
        // },'-',
        {
            itemId:'edit',
            xtype: 'button',
            iconCls:'fa fa-pencil-square-o',
            text: '修改'
        },'-',{
            itemId:'del',
            xtype: 'button',
            iconCls:'fa fa-trash-o',
            text: '删除'
        }
    ],
    columns: [
		{text: 'id', dataIndex: 'id', width:150, menuDisabled: true,hidden:true},
        {text: '档号', dataIndex: 'archivecode', width:150, menuDisabled: true},
        {text: '文件名', dataIndex: 'filename', width:250, menuDisabled: true},
        {text: '扫描件页号', dataIndex: 'scanpagecode', width:100, menuDisabled: true},
        {text: '槗本代码', dataIndex: 'qbcode', width:100, menuDisabled: true},
		{text: '存储路径', dataIndex: 'filepath', width:150, menuDisabled: true},
        {text: '数字化时间', dataIndex: 'digitaltime', width:150, menuDisabled: true},
        {text: '数字化对象描述', dataIndex: 'digitalobjdescribe', width:150, menuDisabled: true},
        {text: '数字化授权描述', dataIndex: 'describeaccreditdescribe', width:150, menuDisabled: true},
	    {text: '格式名称', dataIndex: 'formatname', width:150, menuDisabled: true},
        {text: '格式版本', dataIndex: 'formatversion', width:150, menuDisabled: true},
        {text: '色彩空间', dataIndex: 'colorspace', width:150, menuDisabled: true},
        {text: '压缩方案', dataIndex: 'reduceplan', width:150, menuDisabled: true},
		{text: '压缩比', dataIndex: 'reduceratio', width:150, menuDisabled: true},
        {text: '水平分辨率', dataIndex: 'levelresolution', width:150, menuDisabled: true},
        {text: '垂直分辨率', dataIndex: 'verticalresolution', width:150, menuDisabled: true},
        {text: '设备类型', dataIndex: 'equipmenttype', width:150, menuDisabled: true},
		{text: '设备制造商', dataIndex: 'equipmentmanufacturer', width:150, menuDisabled: true},
        {text: '设备型号', dataIndex: 'equipmentmodel', width:150, menuDisabled: true},
        {text: '设备感光器', dataIndex: 'equipmentsensitization', width:150, menuDisabled: true},
        {text: '数字化软件名称', dataIndex: 'digitalsoftname', width:150, menuDisabled: true},
		{text: '数字化软件版本', dataIndex: 'digitalsoftversion', width:150, menuDisabled: true},
        {text: '数字化软件生产商', dataIndex: 'digitalsoftvendor', width:150, menuDisabled: true},
        {text: '阅读软件硬件条件', dataIndex: 'readsoftcondition', width:150, menuDisabled: true},
        {text: '数字化成果移交信息', dataIndex: 'digitalresultsturnmsg', width:150, menuDisabled: true},
	    {text: '图像宽度', dataIndex: 'picturewidth', width:150, menuDisabled: true},
        {text: '图像高度', dataIndex: 'pictureheight', width:150, menuDisabled: true},
        {text: '位深度', dataIndex: 'bitdepth', width:150, menuDisabled: true},
        {text: '版权', dataIndex: 'copyright', width:150, menuDisabled: true},
		{text: '文件大小', dataIndex: 'filesize', width:150, menuDisabled: true},
        {text: 'MD5', dataIndex: 'md5', width:150, menuDisabled: true},
	]
});