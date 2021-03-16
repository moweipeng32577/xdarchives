/*
 Author：wenjh
 Date：2017.08.03
 Version：desktop 1.0
 */
var sysType = sysType?sysType:'1';
$(function () {
    var panels = [];//窗口集合
    var layeros = [];//窗口实例集合
    var indexs = [];//窗口标识集合
    window.oncontextmenu = function () {
        return false;
    }
    var personalizeds = JSON.parse(personalized);
    if(!personalizeds||!personalizeds.bg||personalizeds.bg==''){
        switch (sysType){
            case '2':$('#zts').attr({'bg':'bg4.jpg'});break;
            case '5':$('#zts').attr({'bg':'bg3.jpg'});break;
            default:$('#zts').attr({'bg':'bg2.jpg'});break;
        }
    }

    if (personalizeds) {
        $('#zts').attr({
            'zts': personalizeds.zts,
            'rgb': personalizeds.rgb,
            'anim': personalizeds.anim,
            'ckb': personalizeds.ckb,
            'bg': personalizeds.bg,
            'ztsize': personalizeds.ztsize
        });
    }
    $('.realname').append(realname,",欢迎您");
    //$('#msgpanel').css({height:(document.body.clientHeight-45)+'px'});
    ////////////////////////////////////////////桌面菜单数据//////////////////////////////////////////////////
    layer.msg('正在准备桌面...', {
        icon: 16
        , shade: 0.5
    });
    var jsondata;//桌面图标数据
    var tbcount = 1;//图标计数器
    /*    $.ajaxSettings.async = false;//同步
     $.getJSON("/geticon",function(data){
     jsondata = data;
     });*/

    $.ajax({
        type: "post",
        url: '/geticon',
        dataType: 'json',
        data:{sysType:sysType},
        success: function (data) {
            jsondata = data;
            if (jsondata.length > 0) {
                tbcount = parseInt(jsondata[jsondata.length - 1].orders) + 1;
            }
        },
        error: function () {
            layer.msg('桌面图标加载失败...', {icon: 5});
        },
        async: false
    });

    layer.closeAll('dialog');

    //强制修改初始密码
    if(changePwd=='true'){
        userMsg(2);
        $('.layui-layer-setwin a').hide();//隐藏关闭按钮
    }else if(changePwd=='new'){//强制定期修改密码
        userMsg(3);
        $('.layui-layer-setwin a').hide();//隐藏关闭按钮
    }

    /////////////////////////////////////////监听鼠标右击事件////////////////////////////////////////////////

    //禁止鼠标右击事件
    // $(document).bind("contextmenu",function(e){
    //    return false;
    //  });


    /*$('.btabs').mousedown(function(e){
     if(3 == e.which){
     alert('右击');
     }else if(1 == e.which){
     //alert('左击');
     }
     });*/

	//隐藏本身所在的平台选项
    if(sysType=='1'){
        $('#zh_zy').hide();
    }else if(sysType=='2'){
        $('#zh_sx').hide();
    }else if(sysType=='3'){
        $('#zh_szh').hide();
    }else if(sysType=='4'){
        $('#zh_jc').hide();
    }else if(sysType=='5'){
        $('#zh_ml').hide();
    }else if(sysType=='6'){
        $('#zh_xw').hide();
    }else if(sysType=='7'){
        $('#zh_by').hide();
    }else if(sysType=='8'){
        $('#zh_kf').hide();
    }

    document.onmousedown = function (e) {

        /*if($(eventnode).attr('id')+''.indexOf('body')>-1||$(eventnode).attr('id')+'s'.indexOf('buttom')>-1){
         $('#menu').fadeOut(100);
         }*/
        //$('#menu').fadeOut(100);

        e = e || event;
        var eventnode = e.srcElement ? e.srcElement : e.target;
        if ($(eventnode).attr("flag") != '0') {
            $('#fk').remove();
        }

        //重构鼠标右击事件
        if (e.button == 2) {

            $("#fk ul li").unbind("click");
            //alert($(eventnode).attr("class")=='btabs');
            if ($(eventnode).attr("flag") != '1') {
                return false;
            }
            $('#menu').fadeOut(100);
            var dindex = '1';
            if (eventnode.nodeName == 'SPAN') {
                dindex = $(eventnode).parent().attr('index');
            } else if (eventnode.nodeName == 'IMG') {
                dindex = $(eventnode).parent().parent().attr('index');
            } else {
                dindex = $(eventnode).attr('index');
            }


            var html = $("<div id='fk'><ul val='" + dindex + "'><li flag='0'><i flag='0' class='icon icon-minus-sign'></i> 关闭全部</li><li flag='0'><i flag='0' class='icon icon-minus-sign'></i> 关闭其他</li><li flag='0'><i flag='0' class='icon icon-minus-sign'></i> 关闭当前</li></ul></div>");

            var lt = $('.btabs')[0].offsetLeft;
            var tp = $('.btabs')[0].offsetTop;
            var x = e.clientX - lt + 50; 		//鼠标移动时获取x轴坐标
            var y = e.clientY - tp - 90; 	//鼠标移动时获取y轴坐标
            html.css({'left': x + 'px', 'top': y + 'px'});
            $('body').append(html);
            $('#fk').fadeIn(150);

            $("#fk ul li").click(function () {
                $('#menu').fadeOut(100);
                $('#fk').remove();
                $("#fk ul li").unbind("click");
                var text = $.trim($(this).text());
                var dindex1 = parseInt($(this).parent().attr("val"));
                if (text == '关闭全部') {
                    for (var di = 0; di < indexs.length; di++) {
                        layer.close(indexs[di]);
                    }
                    $('.btab').fadeOut(200, function () {
                        $(this).remove();
                    });
                    panels = [];
                    layeros = [];
                    indexs = [];
                } else if (text == '关闭其他') {
                    var dindex2 = $.inArray(dindex1, indexs);
                    for (var di1 = 0; di1 < indexs.length; di1++) {

                        if (dindex2 != di1) {
                            layer.close(indexs[di1]);
                            $('#btab' + indexs[di1]).fadeOut(200, function () {
                                $(this).remove();
                            });
                        }

                    }

                    panels = [panels[dindex2]];
                    layeros = [layeros[dindex2]];
                    indexs = [indexs[dindex2]];
                } else {
                    layer.close(dindex1);
                    $('#btab' + dindex1).fadeOut(200, function () {
                        $(this).remove();
                    });
                    panels.splice($.inArray(dindex1, indexs), 1);
                    layeros.splice($.inArray(dindex1, indexs), 1);
                    indexs.splice($.inArray(dindex1, indexs), 1);
                }
            });

            return false;
        }
    }


    $('#body,.btabs').click(function () {
        $('#menu').fadeOut(100);
        $('#msgpanel').animate({'width': '0px'}, 300, function () {
            $('#msgpanel').hide();
        });
    });


    $('#left-tools').click(function () {
        if ($('#menu').css("display") == 'none') {
            $('#menu').fadeIn(100);
        } else {
            $('#menu').fadeOut(100);
        }

    });


    ///////////////////////////////////////菜单/////////////////////////////////////
    var json;
    /*$.ajaxSettings.async = false;//同步
     $.getJSON("/getlist",function(data){
     json = data;
     });*/
    var listUrl = '/getlist';

    if(['1'].indexOf(sysType)<0 && sysType != '11'){
        listUrl = '/unify/getlist?sysType='+sysType+'&realname='+realname+'&loginname='+loginname;
        listUrl =encodeURI(encodeURI(listUrl));
        if(['8'].indexOf(sysType)<0){
            $('.unifyctrl,.imsg').remove();
            $('#msgpanel').remove();
        }
    }

    $.ajax({
        type: "post",
        url: listUrl,
        dataType: 'json',
        success: function (data) {
            json = data;
        },
        error: function () {
            layer.msg('菜单加载失败', {icon: 5});
        },
        async: false
    });

    function myTrim(x) {
        return x.replace(/\s|\xA0/g,"");
    }

    $('.box').ilist({
        data: json,
        click: function (obj) {
            $('#body,.btabs').click();
            var title = obj.text();
            if (obj.attr('isp') == '0') {
                title = obj.parent().parent().parent().find('.leftd').text() + '-' + title;
            } else {
                //title = title.trim() + '-' + title.trim();
            }
            title = myTrim(title);
            if ($.inArray(title, panels) > -1) {
                var index1 = $.inArray(title, panels);
                var zin = parseInt(layer.zIndex) + 1;
                layer.zIndex = zin;
                layeros[index1].css({'z-index': zin + '', 'display': 'block'});
                layer.restore(indexs[index1]);
                return false;
            }

            newpanel(title, obj.find('img').attr('src'), obj.attr("url"));
        }
    });


    $('.box ul li').mousedown(function (e) {
        e.stopPropagation();
        // if(sysType!='1'){//统一平台限制档案系统外的系统添加桌面图标
        //     return;
        // }
        if (3 == e.which) {//判断鼠标右击事件
            // alert('右击');
            //重新加载jsondata防止桌面快捷重复显示
            $.ajax({
                type: "post",
                url: '/geticon',
                dataType: 'json',
                data:{sysType:sysType},
                success: function (data) {
                    jsondata = data;
                },
                error: function () {},
                async: false
            });
            var obj = e.srcElement ? e.srcElement : e.target;
            var li = $(obj).parent().attr("isp") ? $(obj).parent() : $(obj).parent().parent();
            var html = '';
            if (li.find('ul li').length > 0) {

            } else {
                var dkey = 'div[key=' + li.attr("key") + ']';
                if ($(dkey).attr("key")) {
                    return false;
                }

                var tb;
                if ($("#icon" + li.attr("code")).attr("id") && ($(obj).parent().attr("isp") != null)) {/*避免一级导航全放到了第一个菜单下*/
                    html = '<div code="' + li.attr("code") + '" key="' + li.attr("key") + '" url="' + li.attr("url") + '" class="ct0 ianim ianimRight"><div class="close-p"><i class="icon icon-remove-sign icon-close-p" val="' + tbcount + '"></i></div><div class="iicon"><div style="height:30%;"></div><img  class="desktopicon1" src="' + li.find('img').attr("src") + '"></div>' +
                        '<div class="itext">' + li.text() + '</div></div>';
                    $("#icon" + li.attr("code")).append(html);

                    tb = {
                        id: li.attr("key").substring(1),
                        pid: li.attr("code").substring(1),
                        code: li.attr("code"),
                        tkey: li.attr("key"),
                        url: li.attr("url"),
                        icon: li.find('img')?li.find('img').attr("src"):'',
                        text: li.text()!==null?li.text().trim():'',
                        orders: tbcount + ""
                    };
                    jsondata.push(tb);
                } else {
                    var titletext = (li.attr("isp") == '1' ? li.text() : li.parent().parent().parent().find('.leftd').text()).trim();
                    html = '<div id="icon' + li.attr("code") + '" class="" style="padding:50px;" ><div class="cttile">' + titletext +
                        '</div><div  code="' + li.attr("code") + '" key="' + li.attr("key") + '" class="ct0 ianim ianimRight" url="' + li.attr("url") + '" ><div class="close-p"><i class="icon icon-remove-sign icon-close-p" val="' + tbcount + '"></i></div><div class="iicon"><img  class="desktopicon1" src="' + li.find('img').attr("src") + '"></div><div class="itext">' + li.text() + '</div></div>';
                    $(".row").append(html);
                    tb = {
                        id: li.attr("code").substring(1),
                        pid: '0',
                        code: li.attr("code"),
                        tkey: 'k' + li.attr("code").substring(1),
                        url: li.attr("url"),
                        icon: li.find('img').attr("src"),
                        text: titletext,
                        orders: tbcount + ""
                    };
                    jsondata.push(tb);
                    tbcount += 1;
                    tb = {
                        id: li.attr("key").substring(1),
                        pid: li.attr("code").substring(1),
                        code: li.attr("code"),
                        tkey: li.attr("key"),
                        url: li.attr("url"),
                        icon: li.find('img')?li.find('img').attr("src"):'',
                        text: li.text()!==null?li.text().trim():'',
                        orders: tbcount + ""
                    };
                    if (li.attr("isp") == '1') {
                        //tb = {id:li.attr("key").substring(1)+"1",pid:li.attr("code").substring(1),code:li.attr("code"),tkey:li.attr("key")+"1",url:li.attr("url"),icon:li.find('i').attr("class"),text:li.text(),orders:tbcount+""};
                    }
                    jsondata.push(tb);
                };

                $.ajax({
                    type: "post",
                    url: '/user/saveicon?sysType='+sysType,
                    contentType: 'application/json;charset=utf-8', //设置请求头信息
                    data: $.toJSON(jsondata),
                    dataType: 'json',
                    success: function (data) {
                        //layer.msg('图标保存成功...', {icon: 5});
                    },
                    error: function () {
                        //layer.msg('图标保存失败...', {icon: 5});
                    }
                });

                tbcount += 1;
                // $('.ct0').css({background: '#' + $('#zts').attr('ckb')});
            }

            tbclick();
        } else if (1 == e.which) {
            // alert('左击');
        }
    });

    ////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////桌面图标生成区域//////////////////////////

    function zmtb(json) {
        // if(sysType!='1'){//统一平台限制档案系统外的系统加载桌面图标
        //     return;
        // }
        var html = '';
        for (var ci = 0; ci < json.length; ci++) {

            if (json[ci].pid.trim() == '0') {
                if($(".row").find('#icon'+json[ci].code).length==0){
                    if(screen.width==1366||screen.width==1280){
                        html = '<div id="icon' + json[ci].code + '" class="" style="padding:25px;" ><div class="cttile">' + json[ci].text + '</div></div>';
                    }else if(screen.width==1024){
                        html = '<div id="icon' + json[ci].code + '" class="" style="padding:20px;" ><div class="cttile">' + json[ci].text + '</div></div>';
                    }else{
                        html = '<div id="icon' + json[ci].code + '" class="" style="padding:50px;" ><div class="cttile">' + json[ci].text + '</div></div>';
                    }
                    $(".row").append(html);
                }
            }
        }

        for (var ci1 = 0; ci1 < json.length; ci1++) {
            if (json[ci1].pid.trim() != '0') {
                html = '<div code="' + json[ci1].code + '" key="' + json[ci1].tkey + '" url="' + json[ci1].url + '" class="ct0 ianim ianimRight"><div class="close-p"><i class="icon icon-remove-sign icon-close-p" val="' + json[ci1].orders + '"></i></div><div class="iicon"><div style="height:30%;">' +
                    '</div><img  class="desktopicon1" src="' + json[ci1].icon + '"></div><div class="itext">' + json[ci1].text + '</div></div>';
                $("#icon" + json[ci1].code).append(html);
            }
        }

        // $('.ct0').css({background: '#' + $('#zts').attr('ckb')});
        //$('.ct0').animate({'border-radius':'2px'},200);

        tbclick();
    }


    function tbclick() {
        $(".ct0").unbind("click");
        $(".cttile").unbind("click");
        $(".icon-close-p").unbind("click");
        $('.ct0').click(function () {
            var obj = $(this);
            //$('.icon-windows').click();
            var title = obj.text();
            if(obj.parent().find(".cttile").text().trim()!=obj.text().trim()){
                title = obj.parent().find(".cttile").text() + '-' + obj.text();
            }
            title = myTrim(title);
            if ($.inArray(title, panels) > -1) {

                var index1 = $.inArray(title, panels);
                var zin = parseInt(layer.zIndex) + 1;
                layer.zIndex = zin;
                layeros[index1].css({'z-index': zin + '', 'display': 'block'});
                layer.restore(indexs[index1]);

                return false;
            };

            newpanel(title, $(obj.find('img')).attr('src'), obj.attr("url"));
        });

        $('.cttile').click(function (e) {
            e.stopPropagation();
            var display = $(this).parent().find('.icon-close-p').css('display');
            if (display == 'none') {
                $(this).parent().find('.icon-close-p').fadeIn(200);
                $(this).parent().find('.ct0').css({'background':'rgba(255,255,255,.3)'});
            } else {
                $(this).parent().find('.icon-close-p').fadeOut(200);
                $(this).parent().find('.ct0').css({'background':'rgba(255,255,255,0)'});
            }

        });

        $('.icon-close-p').click(function () {
            var flag = "0";
            if ($(this).parent().parent().parent().find('.ct0').length == 1) {
                //$(this).parent().parent().parent().attr()
                flag = $(this).parent().parent().attr("code");
                $(this).parent().parent().parent().remove();

            } else {
                $(this).parent().parent().remove();
            }

            $.ajax({
                type: "post",
                url: '/user/delicon',
                data: {orders: $(this).attr("val"), flag: flag,sysType:sysType},
                dataType: 'json',
                success: function (data) {

                },
                error: function () {

                }
            });
        });
    }

    zmtb(jsondata);

    /////////////////////////////////////////////////////////////////////////////////
    function newpanel(title, icon, url) {
        var i = ifpanel(title, url,icon);
        indexs.push(i);
        panels.push(title);
        setzts();
        var html = "<div class='btab ianim ianimRight' id='btab" + i + "' index='" + i + "' flag='1'><span flag='1'><img flag='1' class='desktopicon2' src='"+icon+"'>" + title + "</span></div>";
        $('.btabs').append(html);
        $('#btab' + i).fadeIn(200);
        //i++;
    };

    $('.btabs').on('click', 'div', function () {
        var index = parseInt($(this).attr('index'));
        index = $.inArray(index, indexs);
        var isdisplay = layeros[index].css('display');

        /////////////////////////获取顶层显示元素///////////////////////
        var copyLayeros = [],topItem=null;
        copyLayeros = copyLayeros.concat(layeros);
        var sortLayeros = copyLayeros.sort(function(a,b){
            return b.css('z-index') - a.css('z-index');
        });
        for(var i=0;i<sortLayeros.length;i++){
            if(sortLayeros[i].css('display')=='block'){
                topItem = sortLayeros[i];
                break;
            }
        }

        // if(sortLayeros.length>1){
        //     var zin = parseInt(layer.zIndex) + 1;
        //     layer.zIndex = zin;
        //     sortLayeros[1].css({'z-index': zin + '', 'display': 'block'});
        // }
        //if (isdisplay == 'block'&&(layeros.length==1||sortLayeros[0].css('z-index')==layeros[index].css('z-index'))) {
        if (isdisplay == 'block'&&(layeros.length==1
            ||layer.zIndex==layeros[index].css('z-index')||topItem==layeros[index])) {
            if (layeros[index].find('.layui-layer-maxmin').length == 0) {
                layer.min(indexs[index]);
                return false;
            }
            //layer.restore(indexs[index]);
            setTimeout(function () {
                layer.min(indexs[index]);
            }, 100);

        } else {
            var zin = parseInt(layer.zIndex) + 1;
            layer.zIndex = zin;
            layeros[index].css({'z-index': zin + '', 'display': 'block'});
            // layer.restore(indexs[index]);
            layer.full(indexs[index]);
        }
    });


    $('.box ul li').hover(function () {
        var wh = $(window).height();
        var eh = $(this).offset().top;
        var uh = $(this).find('.subulli').height();
        var ch = parseFloat(wh) - parseFloat(eh);
        var top = '-37px';
        var rgb = $('#zts').attr('rgb');
        if (ch < parseFloat(uh)) {
            top = '-' + $(this).find('.subulli').height() + 'px';
        }

        /*$(this).css({
         background:'red'
         });*/

        $(this).find('.subulli').css({
            top: top,
            display: 'block'
        });
    }, function () {
        /*$(this).css({
         background:'gray'
         });*/
        $(this).find('.subulli').fadeOut(150);
    });

    function getExtStyle(rgb, ztsize) {
        var style = "<style>.x-panel-header-default,.x-window-header-default-top,.x-btn-default-small,.x-window-default," +
            ".x-tab-bar-default,.x-panel-default-framed,.x-tool-img,.x-panel-default-outer-border-rbl{background-color:rgba(" + rgb + ",0.7) !important;border-color:rgba(" + rgb + ",0.7) !important;}" +
            ".x-form-text,.x-tree-node-text ,.x-btn-icon-small-left,.x-btn-text,.x-btn-inner,.x-column-header-text-inner,.x-grid-td,.x-form-item-label-text,label,.x-window-text,.x-title-text-default{font-size:" + ztsize + " !important;}" +
            ".x-autocontainer-innerCt,.x-toolbar-text,.x-component,.x-progress-text,.x-form-display-field,.x-tab-inner-default,.webuploader-pick{font-size:" + ztsize + " !important;}.x-btn-over,.x-boundlist-item-over,.x-menu-item-focus,.x-menu-item-active{background-color:rgba(" + rgb + ",0.6) !important;border-color:rgba(" + rgb + ",0.6)!important;}.x-boundlist-selected{background-color:rgba(" + rgb + ",0.8) !important;border-color:rgba(" + rgb + ",0.8) !important;}" +
            ".x-grid-item-focused{border-color:rgba(" + rgb + ",1) !important;border:1px solid rgba(" + rgb + ",1) !important;} .x-tree-view{overflow-x: auto !important;} .x-boundlist-item{font-size:"+ztsize+"}" +
            ".listlibg{background:(" + rgb + ",0.7)!important;} .webuploader-pick{padding:10px 5px !important;}"+
            "</style>";
        return style;
    }


    function ifpanel(titl, url,imgpath) {
        var anim = $('#zts').attr('anim');
        var codeurl =encodeURI(encodeURI(url));
        if(url=="/appraisal/main"){//到期鉴定需添加
            titl+="<span style='padding-left:10px;' class='appraisalNumberTip'></span>"
        }
        var index = layer.open({
            title: '<img class="layerTitleIcon" src="'+imgpath+'"/>'+titl,
            anim: anim,
            type: 2,
            skin: 'layui-layer-lan',
            shade: 0,
            content: codeurl,
            offset: '0px',
            area: ['80%', '80%'],
            maxmin: true,
            moveOut: false,
            moveType: 0,
            zIndex: layer.zIndex,
            success: function (layero, index) {
                layer.setTop(layero);
                var rgb = $('#zts').attr('rgb');
                var ztsize = $("#zts").attr("ztsize");
                try{
                    if (layer.getChildFrame('.icenter').length == 0) {
                        layer.getChildFrame('style', index).remove();
                        layer.getChildFrame('head', index).append(getExtStyle(rgb, ztsize));
                    }
                }catch (err){
                    console.log(err);
                }
            },
            min: function (layero, index) {
                //layero.css({'display':'none'});
                //layero.hide();
                //$('#btab'+index).attr('isfirst','0');
            },
            full: function (layero, index) {
                //var body = layer.getChildFrame('body', index);
                //var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：

                //body.find("#div1").click();

            },
            restore: function (layero, index) {
                //var body = layer.getChildFrame('body', index);
                //var iframeWin = window[layero.find('iframe')[0]['name']]; //得到iframe页的窗口对象，执行iframe页的方法：
                //body.find("#div1").click();
            },
            cancel: function (index) {
                //delete $scope.panels[$.inArray(index,$scope.indexs)];
                $('#btab' + index).fadeOut(200, function () {
                    $(this).remove();
                });
                panels.splice($.inArray(index, indexs), 1);
                layeros.splice($.inArray(index, indexs), 1);
                indexs.splice($.inArray(index, indexs), 1);
            }
        });
        changeCloseWz();
        layer.full(index);
        layeros.push(layer.getlayero(index));
        return index;
    }


    function ifpanel1(titl, url, x, y, type, isclose) {
        var index = layer.open({
            title: titl,
            type: 2,
            skin: 'layui-layer-lan',
            shade: 0,
            offset: '100px',
            content: url,
            zIndex: layer.zIndex,
            area: [x, y],
            shade: 0.3,
            resize: false,
            closeBtn: isclose?0:1,
            success: function (layero, index) {
                if (layer.getChildFrame('.logotop').length > 0) {
                    setTimeout(function(){
                        location.href = "/index";
                    },500);
                }
                if (type == '壁纸') {
                	$('#menu').fadeOut(100);
                }
            }
        });
        changeCloseWz();
        setzts1();
        return index;
    }

    function changeCloseWz(){
        var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
        var isIE = (userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1)||(userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0") > -1); //判断是否IE<11浏览器
        if(isIE){
            $('.layui-layer-setwin .layui-layer-close1 cite').css({'top':'-10px'});
        }
    }
    function setzts1() {
        var zts = '#' + $('#zts').attr('zts');
        var rgb = $('#zts').attr('rgb');
        var ztsize = $("#zts").attr("ztsize");
        $('.layui-layer-title').css({
            color: '#fff',
            background: zts
        });

        // $('.list-group-item a').css({
        //     color:zts
        // });

        $('.layui-layer-iframe').css({
           border:'5px solid '+zts
        });

        $('#buttom').css({
            background: 'rgba(' + rgb + ',0.9)'
        });

        $('#menu').css({
            border:'5px solid rgba(' + rgb + ',.8)'
        });

        $('.menuright,.isgxhli').css({
            background: 'rgba(' + rgb + ',.8)'
        });

        $('.menuright,.isptqhli').css({
            background: 'rgba(' + rgb + ',.8)'
        });

        // $('.menuleft,.box ul li ul').css({
        //     background:'linear-gradient(to left, rgba('+rgb+',0.4), rgba('+rgb+',0.9))'
        // });

        $('.ititle').css({
            background: zts
        });

        // $("#menu").css({
        //     'border-top': '6px solid' + zts,
        //     'border-right': '6px solid' + zts
        // });

        // $('.listlibg').css({ background: 'rgba(' + rgb + ',0.3)'});

        $('.layui-layer-title').css({'font-size': ztsize});
        for (var t = 0; t < indexs.length; t++) {
            layer.getChildFrame('style', indexs[t]).remove();
            layer.getChildFrame('head', indexs[t]).append(getExtStyle(rgb, ztsize));
        }

    }

    function setzts() {
        try{
            setzts1();
            $('#body').css({
                'background-image': 'url(../../img/background/' + $("#zts").attr("bg") + ')'
            });

            $('body').css({
                'font-size': $("#zts").attr("ztsize")
            });
        }catch (err){
            console.log(err);
        }
    }

    $('#zts').click(function () {
        setzts();
        var data = {
            zts: $("#zts").attr("zts"),
            rgb: $("#zts").attr("rgb"),
            anim: $("#zts").attr("anim"),
            ckb: $("#zts").attr("ckb"),
            bg: $("#zts").attr("bg"),
            ztsize: $("#zts").attr("ztsize")
        };
        $.ajax({
            type: "post",
            url: '/user/upPersonalized',
            data: data,
            dataType: 'json',
            success: function (data) {
                console.log('成功');
            },
            error: function () {
                console.log('失败');
            }
        });
    });


    $(".userheadimg").click(function (e) {
        //$("ul li:first-child").attr("linum",7);
        e.stopPropagation();
        $('#body,.btabs').click();
        userimg();
    });
    // $(".realname").click(function () {
    //     $("ul li:first-child").attr("linum",0);
    // });
    $(".list-group-item").click(function (e) {
        e.stopPropagation();
        var linum = parseInt($(this).attr("linum"));
        if(isNaN(linum)){
            return;
        }
        $('#body,.btabs').click();
        switch (linum) {
            case 0:
                userMsg(1);
                break;
            case 1:
                zt();
                break;
            case 2:
                anim();
                break;
            case 3:
                backg();
                break;
            case 4:
                logout();
                break;
            case 5:
                setzt();
                break;
            case 6:
                $.ajax({
                    type: "post",
                    url: '/user/platformchange',
                    data:{'userid':userid,
                        'changetype':"0"
                    },
                    success: function (result) {
                    }
                });
                location.href = "/index?sysType=0";
                break;
            case 7:
               // userimg();
                break;
            case 8:
                window.open(userDoc);
                break;
            case 9:
                location.href = '/switchDesktop';
                break;

            case 21:
                location.href = "/index?sysType=1";//档案资源管理系统
                break;
            case 22:
                location.href = "/index?sysType=2";//声像系统
                break;
            case 23:
                var permiss = isPermissed(loginname, 3);
                if (permiss) {
                    layer.msg('用户没有该平台权限');
                    return;
                }
                location.href = "/index?sysType=3";//数字化加工辅助工具
                break;
            case 24:
                var permiss = isPermissed(loginname, 4);
                if (permiss) {
                    layer.msg('该用户没有该平台权限');
                    return;
                }
                location.href = "/index?sysType=4";//基础平台系统
                break;
            case 25:
                var permiss = isPermissed(loginname, 5);
                if (permiss) {
                    layer.msg('该用户没有该平台权限');
                    return;
                }
                location.href = "/index?sysType=5";//目录中心系统
                break;
            case 26:
                var permiss = isPermissed(loginname, 6);
                if (permiss) {
                    layer.msg('该用户没有该平台权限');
                    return;
                }
                location.href = "/index?sysType=6";//新闻影像采集归档管理系统
                break;
            case 27:
                var permiss = isPermissed(loginname, 7);
                if (permiss) {
                    layer.msg('该用户没有该平台权限');
                    return;
                }
                location.href = "/index?sysType=7";//编研管理系统
                break;
            case 28:
                var permiss = isPermissed(loginname, 8);
                if (permiss) {
                    layer.msg('该用户没有该平台权限');
                    return;
                }
                location.href = "/index?sysType=8";//库房管理系统
                break;
            case 29:
                location.href = "/index?sysType=0";//自助查询系统
                break;

            default:
                console.log(linum);
        }
    });

     $('.gxhli').hover(function(){
        $('.isgxhli').fadeIn(200);
     },function(){
         $('.isgxhli').fadeOut(200);
     })
    // $('.ptqhli').hover(function(){
    //     $('.isptqhli').fadeIn(200);
    // },function(){
    //     $('.isptqhli').fadeOut(200);
    // })
    $('.ptqhli').click(function(){
        location.href = "/indexswitch?sysType="+sysType;
    })

    function userMsg(type) {
        var title='用户信息';
        if(type==2){
            title='修改初始密码';
        }else if(type==3){
            title='定期更新密码';
        }
        ifpanel1(title, '/user/userMsg', '370px', '350px');
    }

    function zt() {
        ifpanel1('主 题', '/user/zt', '360px', '260px');
    }

    function anim() {
        ifpanel1('动 效', '/user/anim', '700px', '550px');
    }

    function backg() {
        ifpanel1('壁 纸', '/user/userbg', '1000px', '550px', '壁纸');
    }

    function setzt() {
        ifpanel1('字体大小', '/user/ztsize', '270px', '150px');
    }

    function userimg() {
        ifpanel1('个性头像', '/user/userimg', '500px', '580px');
    }

    function logout() {
        layer.confirm('确定退出？', {
            title: '提示',
            offset: '30%',
            zIndex: layer.zIndex,
            btn: ['确定', '取消'] //按钮
        }, function () {
            $.ajaxSettings.async = false;//同步
            //$("#logout").click();
            location.href = "/logoutt";
            //layer.msg('退出成功', {icon: 1});
        }, function () {
            layer.msg('取消退出', {icon: 2});
        });
        //setzts();
    }

    function isPermissed(loginname,logintype){
        var ran=Math.random();
        var permiss=false;
        var url='/isPermiss?loginname='+loginname+'&logintype='+logintype+'&t='+ran;
        $.ajax({
            type: "post",
            url: url,
            async: false,
            dataType: 'json',
            success: function (data) {
                permiss = data.success;
            },
            error: function () {
                layer.msg('登录权限错误！', {icon: 5});
            }
        });
        return permiss;
    }

    var imsgindex = 0;

    /*	var interval = setInterval(function(){
     if($('.imsg').css("display")=='none'){
     $('.imsg').fadeIn(200);
     }else{
     $('.imsg').fadeOut(200);
     }
     },500);*/

    $('.imsg').click(function () {
        if ($('#msgpanel').css('display') == 'none') {
            $('#msgpanel').show();
            $('#msgpanel').animate({'width': '350px'}, 250);
        } else {
            $('#msgpanel').animate({'width': '0px'}, 300, function () {
                $('#msgpanel').hide();
            });
        }
    });

    /*	$('.imsg').click(function(){
     //window.clearInterval(interval);
     $('.imsg').fadeIn(200);
     var wh = $(window).height()-345;
     var ww = $(window).width()-250;
     if(imsgindex!=0){
     layer.close(imsgindex);
     }
     imsgindex = layer.open({
     title:'信息',
     type: 1,
     anim:'2',
     skin:'layui-layer-lan',
     offset:[wh+'px',ww+'px'],
     content: '<center><div style="margin-top:30%">暂无信息..</div></center>',
     zIndex:layer.zIndex,
     area: ['250px','300px'],
     shade:0,
     resize:false
     });
     });*/


    $('.rbzy').click(function () {
        for (var di3 = 0; di3 < indexs.length; di3++) {
            //layer.restore(indexs[di3]);
            //setTimeout(function(){
            if (parseInt(layeros[di3].css('height').substring(0, layeros[di3].css('height').length - 2)) > 50) {
                layer.min(indexs[di3]);
            }

            //},100);
        }
    });
    var msgurl;

    if(sysType=='8'){
        msgurl = '/user/getOutWareMsg'
    }else{
        msgurl =  '/user/getmsg';
    }

    var imsg = $('#msgpanel').imsg({
        url: msgurl,
        clicktr: function (obj) {
            $('#body,.btabs').click();
            if (obj.find('td')[0].innerText.indexOf('公 告') > -1) {
                ifpanel1('查看公告', '/user/inform?msgid=' + obj.attr('msgid'), '700px', '420px');
                clearMsg(obj, obj.attr('msgid'), "公告");
                return;
            }

            //没有数据就自动清除无用的代办事项
            var tasktype = obj.find('td')[0].innerText.replace(':', '').trim();
            $.ajax({
                type: "post",
                url: '/user/deletetask',
                data: {tasktype:tasktype,taskid:obj.attr('msgid').trim()},
                dataType: 'json',
                async: false,
                success: function (data) {
                    if(data.success){
                        layer.msg('该代办事项已无效', {icon: 2});
                    }
                },
                error: function () {
                    layer.msg('操作失败', {icon: 2});
                }
            });

            //后面追加的四个数据，是为了防止窗口重复（窗口是以文本区分的，如果不加这些数字的话就重复了，重复了会导致桌面某些地方操作混乱）
            var text = obj.find('td')[0].innerText.replace(':', '审批') + obj.attr('msgid').substring(2, 4) + obj.attr('msgid').substring(10, 12) + obj.attr('msgid').substring(obj.attr('msgid').length-2, obj.attr('msgid').length);
            if ($.inArray(text, panels) > -1) {
                var index1 = $.inArray(text, panels);
                var zin = parseInt(layer.zIndex) + 1;
                layer.zIndex = zin;
                layeros[index1].css({'z-index': zin + '', 'display': 'block'});
                layer.restore(indexs[index1]);
                return false;
            }

            if (text.indexOf('销毁审批') > -1) {
                newpanel(text, 'img/icon/鉴定与销毁.png', '/destructionBill/billApproval?taskid=' + obj.attr('msgid').trim());
            } else if (text.indexOf('实体查档审批') > -1) {
                newpanel(text, 'img/icon/查档管理.png', '/stApprove/main?taskid=' + obj.attr('msgid').trim());
            } else if (text.indexOf('数据开放') > -1) {
                newpanel(text, 'img/icon/查档管理.png', '/openApprove/main?taskid=' + obj.attr('msgid').trim());
            } else if (text.indexOf('磁盘存储空间不足提醒') > -1) {
                newpanel(text.split("审批")[0]+"窗口", 'img/icon/查档管理.png', '/diskspace/main?taskid=' + obj.attr('msgid').trim());
            } else if (text.indexOf('查档到期提醒') > -1) {
                $.ajax({
                    type: "post",
                    url: '/jyAdmins/checkExpireGhEntry',
                    data: {flag:'未归还',page:1,start:0,limit:50},
                    dataType: 'json',
                    success: function (data) {
                        newpanel(text.split("审批")[0]+"窗口", 'img/icon/查档管理.png', '/jyAdmins/ghmain?taskid='+obj.attr('msgid').trim()+'&borrowmsgid='+obj.attr('borrowmsgid').trim());
                        if(!(data.data&&data.data=='1')){
                            layer.msg('无未归还记录', {icon: 2});
                            clearMsg(obj.attr('msgid'));
                        }
                    },
                    error: function () {
                        layer.msg('操作失败', {icon: 2});
                    }
                });

            }else if (text.indexOf('查档审批') > -1){
                newpanel(text, 'img/icon/查档管理.png', '/electronApprove/main?taskid=' + obj.attr('msgid').trim());
            }else if (text.indexOf('审核入库成功提醒') > -1){
                newpanel(text, 'img/icon/数据管理.png', '/management/main?taskid=' + obj.attr('msgid').trim()+'&isp=k8');
            }else if (text.indexOf('预约提醒') > -1){
                newpanel(text, 'img/icon/预约管理.png', '/jyAdmins/yymainly?taskid=' + obj.attr('msgid').trim()+'&isp=k69');
            }else if (text.indexOf('电子打印审批') > -1){
                newpanel(text, 'img/icon/查档管理.png', '/electronPrintApprove/main?taskid=' + obj.attr('msgid').trim());
            }else if (text.indexOf('公车预约') > -1){
                newpanel(text, 'img/icon/公车预约.png', '/carOrder/main?taskid=' + obj.attr('msgid').trim()+'&isp=k209');
            }else if (text.indexOf('场地预约') > -1){
                newpanel(text, 'img/icon/公车预约.png', '/placeOrder/main?taskid=' + obj.attr('msgid').trim()+'&isp=k211');
            }else if(text.indexOf('采集移交审核') > -1){
                newpanel(text, 'img/icon/数据审核.png', '/audit/mainDeal?taskid=' + obj.attr('msgid').trim());
            }else if(text.indexOf('实体出库') > -1){
                //判断实体档案是否出库，若出库，更新任务状态
                $.ajax({
                    type: "post",
                    url: '/jyAdmins/isBorrowdocOutware',
                    data: {
                        borrowcode:obj.attr('msgid')
                    },
                    dataType: 'json',
                    success: function (resultData) {
                        var data = resultData.data;
                        if(data.outwarestate=='已借出'){
                            layer.msg('已完成出库，无需重复出库！', {icon: 5});
                        }else{
                            newpanel(text, 'img/icon/实体档案出库.png', '/outware/main?borrowcode=' + obj.attr('msgid').trim());
                        }
                    },
                    error: function () {
                        layer.msg('操作失败', {icon: 5});
                    }
                });
            }else if(text.indexOf("到期鉴定提醒")>-1){
                $.ajax({
                    type: "post",
                    url: '/user/deleteTask',
                    data: {
                        msgId: obj.attr('msgid')
                    },
                    dataType: 'json',
                    success: function (resultData) {
                        clearMsg(obj, obj.attr('msgid'), "待办事项");
                        newpanel(text, 'img/icon/到期鉴定管理.png', '/appraisal/main');
                    },error: function () {
                        layer.msg('操作失败', {icon: 5});
                    }
                })
            }else if (text.indexOf('年检') > -1){
                newpanel(text, 'img/icon/年检审核.png', '/yearlyCheckAudit/main?taskid=' + obj.attr('msgid').trim());
            }
        },
        clicki: function (obj) {
            var text = obj.find('td')[0].innerText.replace(':', '审批') + obj.attr('msgid').substring(2, 3) + obj.attr('msgid').substring(10, 12) + obj.attr('msgid').substring(30, 31);
            if (text.indexOf('公 告') > -1) {
            	clearMsg(obj, obj.attr('msgid'), "公告");
            } else {
//        		layer.confirm('是否要删除该提醒？', {
//	                title: '提示',
//	                offset: '30%',
//	                zIndex: layer.zIndex,
//	                btn: ['确定', '取消'] //按钮
//	            }, function () {
	                if (text.indexOf('查档到期提醒') > -1) {
	                	clearMsg(obj, obj.attr('msgid'), "待办事项");
	            	}
//	            }, function () {
//	                layer.msg('取消删除', {icon: 2});
//	            });
        	}
        }
    });

    function clearMsg(obj ,id, type){
    	var url = '';
    	if (type == '公告') {
    		url = '/inform/clearInform';
    	} else {
    		url = '/stApprove/deleteTask';
    	}
        $.ajax({
            type: "post",
            url: url,
            data: {id: id},
            dataType: 'json',
            async: false,
            success: function (data) {
            	if (type == '查档到期提醒') {
            		layer.msg('删除成功', {icon: 2});
            	}
                obj.remove();
                imsg.msgcount--;
                setmsgnum();
            },
            error: function () {
            	if (type == '查档到期提醒') {
                	layer.msg('删除失败', {icon: 2});
            	}
            }
        });
    }

    window.closeObj = {
        close: function (index) {
            if (index) {
                $('#btab' + index).fadeOut(200, function () {
                    $(this).remove();
                });
                index = parseInt(index);
                panels.splice($.inArray(index, indexs), 1);
                layeros.splice($.inArray(index, indexs), 1);
                indexs.splice($.inArray(index, indexs), 1);
                layer.close(index);
            }
            imsg.refresh();
            setmsgnum();
            setzts();
        },

        setSubWinFontSize:function(win){
            var rgb = $('#zts').attr('rgb');
            var ztsize = $("#zts").attr("ztsize");
            var _iframe = win.contentWindow;
            var styleEle = _iframe.document.getElementsByTagName('style');
            var headEle = _iframe.document.getElementsByTagName('head');
            console.log(headEle);
            if(styleEle.length>0){
                $(styleEle[0]).remove();
            }
            $(headEle[0]).append(getExtStyle(rgb, ztsize));
        },

        changeHeadPortrait:function (path) {
            $(".userheadimg").attr("src",path);
        },

        getStyle:function(){
            var rgb = $('#zts').attr('rgb');
            var ztsize = $("#zts").attr("ztsize");
            return getExtStyle(rgb, ztsize);
        }
    }

    function setmsgnum() {
        if (imsg.msgcount == 0) {
            $('.msgnum').html(imsg.msgcount).hide();
        } else {
            $('.msgnum').show().html(imsg.msgcount);
        }
    }

    setzts();//个性化
    setmsgnum();
    //setInterval(function(){imsg.refresh();setmsgnum();setzts();},3000);
    var socket = new SockJS("/websocket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        stompClient.subscribe('/message', function(respnose){
            imsg.refresh();setmsgnum();setzts();
        });
        stompClient.subscribe('/user/'+userid+'/message', function(respnose){
            imsg.refresh();setmsgnum();setzts();
        });
    });

    socket.onclose = function() {
        setTimeout(function(){
            location.href = "/index";
        },3000);
    };

    $(window).resize(function () {
        panelSize();
    });

    function panelSize() {
        $('#msgpanel').height($(window).height()-$('#buttom').height()-20);
        $('#msgpaneldiv').height($(window).height()-$('#buttom').height()-20);
    }
    panelSize();

    // if(loginname=='xitong'||loginname=='aqbm'||loginname=='aqsj'){
    //     $.ajax({
    //         type: "post",
    //         url: '/checkInit',
    //         data: {},
    //         dataType: 'json',
    //         success: function (data) {
    //             var inits = data.data.split(',');
    //             var flag = false;
    //             for(var i in inits){
    //                 if(inits[i]=='false'){
    //                     flag = true;
    //                     continue;
    //                 }
    //             }
    //
    //             if(flag){
    //                 ifpanel1('初始化信息', '/initialize?init='+data.data, '450px', '400px',true);
    //             }
    //         },
    //         error: function () {
    //
    //         }
    //     });
    // }

});

