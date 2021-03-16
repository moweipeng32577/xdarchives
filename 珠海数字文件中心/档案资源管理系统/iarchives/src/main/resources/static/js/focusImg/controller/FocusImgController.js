/**
 * Created by xd on 2017/10/21.
 */
Ext.define('FocusImg.controller.FocusImgController', {
     extend: 'Ext.app.Controller',

    // 其实翻译出来就是“从根 app 开始找 view（注意没带 s 哦） 目录，在这个目录下找到 student 目录，然后加载 List.js 这个文件”
    views: ['FocusImgView','ElectronicView'],//加载view
    stores: [],//加载store
    models: [],//加载model
    init: function () {
        this.control({
            'focusImgView': {
                afterrender: function (view) {
                    var eleview = view.down('electronic');
                    eleview.initData('');
                    var buttons = eleview.down('toolbar').query('button');
                    //var treepanel = eleview.down('treepanel');
                    // treepanel.getStore().root.text = '焦点图列表';
                    // treepanel.proxy.url = '/electronic/';
                    for(var i=0;i<buttons.length;i++){
                        if(buttons[i].text=='上传'||buttons[i].text=='删除'||buttons[i].text=='上移'||buttons[i].text=='下移'){
                            continue;
                        }
                         buttons[i].hide();
                    }
                    //view.setActiveTab(1);
                }
            }
        });
    }
});