/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Classificationsetting.model.ClassificationsettingGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string', mapping: 'classid'},
        {name: 'classname', type: 'string'},
        {name: 'code', type: 'string'},
        {
            name: 'classlevel',
            type: 'string',
            convert: function (value, record) {
                var level = record.get('classlevel');
                if (level === 1) {
                    value = "卷内管理";
                } else if (level === 2) {
                    value = "案卷管理";
                } else if (level === 3) {
                    value = "未归管理";
                } else if (level === 4) {
                    value = "已归管理";
                } else if (level === 5) {
                    value = "资料管理";
                } else if (level === 6) {
                    value = "文件管理";
                } else if (level === 7) {
                    value = "全宗卷管理";
                } else if (level === 8) {
                    value = "编研采集";
                } else if (level === 9) {
                    value = "声像档案";
                } else {
                    value = "";
                }
                return value;
            }
        }
    ]
});