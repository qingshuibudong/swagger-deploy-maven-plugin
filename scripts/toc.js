(function(){


    //返回一个动词（GET、POST）请求的信息
    var getRequest=function($verb){
        var ret={
            id:'',
            title:'',
            method:$verb.text()
        };
        var $dom=$verb.next();
        if($dom.is('p')){
            var a=$dom.children().first();
            ret.id=a.attr('id');
            ret.title=a.text()
        }

        return ret;
    };

    //返回一个url下，所有的动词的请求信息
    var getVerbs=function($urlDom){
       var all=$urlDom.nextUntil('h2');
       var verbs=all.filter("h3");
       var ret=[];
       $.each(verbs,function(i,item){
           ret.push(getRequest($(item)));
       });
        return ret;
    };

    var genMenu=function(container){
        var menu=$('<ul></ul>').css('list-style','none');

        //去掉第一个，不是接口
        $.each(container.children('h2:gt(0)'),function(i,val){
            //每一个接口url
            var $val=$(val);
            //有子节点的，不是接口
            if($val.children().length>0){
                return
            }
            var item=$('<li></li>').text($val.text());
            var ul=$('<ul></ul>');
            item.append(ul);

            //添加每一个动词与说明
            var verbs=getVerbs($val);
            $.each(verbs,function(i,verb){
                var link=$("<a></a>").attr('href',"#"+verb.id).text(verb.method+"  "+verb.title);
                var li=$('<li></li>');
                li.append(link);

                ul.append(li);
            });
            menu.append(item);
        });
        return menu;
    };

    var container=$('#content');

    var menu=genMenu(container);
    var menuContainer=$('<div class="menu-container"></div>');
    menuContainer.append(menu);

    var newContainer=$("<div id='newContainer'><div>");
    container.before(newContainer);
    newContainer.prepend(menuContainer,container);

    menuContainer
        .css('border','solid 1px color:#ccc')
        .css('width',"350px")
        .css('position','fixed');
    container.css('margin-left','400px');


})();
