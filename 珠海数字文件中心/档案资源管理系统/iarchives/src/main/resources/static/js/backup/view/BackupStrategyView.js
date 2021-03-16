/**
 * Created by RonJiang on 2018/3/23 0023.
 */
Ext.define('Backup.view.BackupStrategyView',{
    extend: 'Ext.form.Panel',
    xtype:'backupStrategyView',
    layout:'column',
    items:[{
        columnWidth:1,
        xtype:'combo',
        name:'backupfrequency',
        itemId:'backupFrequency',
        fieldLabel:'备份频率',
        editable: false,
        store:[
            ['everyday','每日'],
            ['MON','每周一'],
            ['TUE','每周二'],
            ['WED','每周三'],
            ['THU','每周四'],
            ['FRI','每周五'],
            ['SAT','每周六'],
            ['SUN','每周日']
        ],
        margin:'20 20 5 10',
        allowBlack:false,
        afterLabelTextTpl: [
            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
        ]
    },{
        columnWidth:1,
        xtype:'combo',
        name:'backuptime',
        itemId:'backupTime',
        fieldLabel:'备份时间',
        editable: false,
        store:[
            ['8','8时'],
            ['12','12时'],
            ['18','18时'],
            ['21','21时'],
            ['23','23时']
        ],
        margin:'20 20 5 10',
        allowBlack:false,
        afterLabelTextTpl: [
            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
        ]
    },{
        columnWidth:1,
        xtype:'combo',
        name:'backuptype',
        itemId:'backupType',
        fieldLabel:'备份方式',
        editable: false,
        store:[
            ['fullbackup','完全备份']
            // ,['incrementalbackup','增量备份']
            // ,['differentialbackup','差异备份']
        ],
        margin:'20 20 5 10',
        allowBlack:false,
        afterLabelTextTpl: [
            '<span style="color:red;font-weight:bold" data-qtip="必填项">*</span>'
        ]
    }],
    buttons:[{
        text: '保存',
        itemId: 'backupStrategySave'
    }, '-',{
        text: '返回',
        itemId: 'backupStrategyBack'
    }]
});