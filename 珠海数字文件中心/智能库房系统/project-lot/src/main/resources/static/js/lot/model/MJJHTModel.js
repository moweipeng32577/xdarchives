/**
 * Created by Rong on 2019-03-01.
 */
Ext.define('Lot.model.MJJHTModel',{
    extend:'Ext.data.Model',
    fields:[{
        name:'tem',mapping:'captureValue',
        convert:function(value, record){
            var tem = Ext.decode(value).tem + '℃';
            return tem;
        }
    },{
        name:'hum',mapping:'captureValue',
        convert:function(value, record){
            var hum = Ext.decode(value).hum + '%';
            return hum;
        }
    }]
});