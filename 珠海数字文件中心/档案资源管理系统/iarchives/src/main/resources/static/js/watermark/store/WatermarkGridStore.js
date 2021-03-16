/**
 * Created by tanly on 2017/11/1 0024.
 */
Ext.define('Watermark.store.WatermarkGridStore', {
    extend: 'Ext.data.Store',
    model: 'Watermark.model.WatermarkGridModel',
    pageSize: XD.pageSize,
    proxy: {
        type: 'ajax',
        url: '/watermark/findWatermarkBySearch',
        reader: {
            type: 'json',
            rootProperty: 'content',
            totalProperty: 'totalElements'
        }
    }
});