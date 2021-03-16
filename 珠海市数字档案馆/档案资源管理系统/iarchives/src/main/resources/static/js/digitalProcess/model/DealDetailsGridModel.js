Ext.define('DigitalProcess.model.DealDetailsGridModel',{
    extend:'Ext.data.Model',
    fields: [
        {name: 'id', type: 'string'},
        {name: 'batchcode', type: 'string'},
        {name: 'calloutid', type: 'string'},
        {name: 'nodename', type: 'string'},
        {name: 'operator', type: 'string'},
        {name: 'operatetime', convert: function(value, record) {
            if(!value){
                return "";
            }
            var year = value.substring(0,4);
            var month = value.substring(4,7).replace("-",'');
            var day = value.substring(7,10).replace("-",'');
            // var time = value.substring(10,12);
            var minute = value.substring(10);
            return year+'年'+month+"月"+day+"日 "+minute;
        }}
    ]
})
