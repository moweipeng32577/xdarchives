/**
 * Created by tanly on 2017/11/17 0002.
 */
Ext.define('FullSearch.controller.FullSearchController', {
    extend: 'Ext.app.Controller',
    views: ['FullSearchView', 'FullSearchGridView'],
    stores: ['FullSearchGridStore'],
    models: ['FullSearchGridModel'],
    init: function () {
        this.control({
            'fullSearchView [itemId=fullSearchSearchfieldId]': {
                search: function (searchfield) {
                    var fullSearchInputView = searchfield.findParentByType('panel');
                    var fullSearchView = fullSearchInputView.findParentByType('fullSearchView');
                    var grid = fullSearchView.down('fullSearchGridView');
                    // grid.getStore().on('load',{callback:function(result){
                    //     console.log(result)
                    //     if(result.getCount()==0){
                    //         XD.msg('没有找到相关内容！');
                    //     }
                    // }});
                    var searchValue = searchfield.getValue();
                    var inresult = fullSearchView.down('[itemId=inresult]').getValue();
                    var oldparams = '';
                    if (inresult) {
                        oldparams = grid.dataParams.filters
                    }
                    Ext.Ajax.request({
                        url: '/fullSearch/searchValidation',//
                        async: false,
                        params: {
                            filters: searchValue,
                            oldparams: oldparams,
                            iflag:iflag
                        },
                        timeout:XD.timeout,
                        success: function (response) {
                            var respText = Ext.decode(response.responseText);
                            if (respText.success == true) {
                                grid.initGrid({filters: searchValue, oldparams: oldparams,iflag:iflag});//开始检索
                            } else {
                                XD.msg(respText.msg);
                                grid.getStore().removeAll();
                            }
                        }
                    });
                }
            },
            'EntryFormView button[itemId="back"]': {
                click: function (view) {
                    window.fullSearchShowWins.close();
                }
            }
        });
    }
});