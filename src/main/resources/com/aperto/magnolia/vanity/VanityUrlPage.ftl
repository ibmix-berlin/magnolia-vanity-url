<!DOCTYPE html>
<html>
	<head>
        <title>${this.messages.get("page.headline")}</title>
        <script type="text/javascript" src="${this.request.contextPath}/.resources/magnolia-vanity-url/jquery-1.4.2.js"></script>
        <script type="text/javascript" src="${this.request.contextPath}/.resources/magnolia-vanity-url/jquery.sortElements.js"></script>
        <script type="text/javascript">
            $(function () {
            var table = $('table');
                $('th', table).each(function() {
                    var th = $(this), thIndex = th.index(), inverse = false;

                    th.click(function() {
                        var jElem = $(this);
                        jElem.removeClass('asc');
                        jElem.siblings().removeClass('asc');
                        jElem.removeClass('desc');
                        jElem.siblings().removeClass('desc');
                        if (inverse) {
                            jElem.addClass('desc');
                        } else {
                            jElem.addClass('asc');
                        }
                        table.find('td').filter(
                                function() {
                                    return $(this).index() === thIndex;
                                }).sortElements(function(a, b) {
                                    return $.trim($.text([a])) > $.trim($.text([b])) ? inverse ? -1 : 1 : inverse ? 1 : -1;
                                }, function() {
                                    // parentNode is the element we want to move
                                    return this.parentNode;
                                });
                        inverse = !inverse;
                    });
                });
            });
        </script>
        <link href="${this.request.contextPath}/.resources/admin-css/admin-all.css" type="text/css" rel="stylesheet" />
        <style type="text/css">
            th {
                background: url(${this.request.contextPath}/.resources/magnolia-vanity-url/sort_bg.gif) no-repeat right;
            }
            th.asc {
                background: url(${this.request.contextPath}/.resources/magnolia-vanity-url/asc.gif) no-repeat right;
            }
            th.desc {
                background: url(${this.request.contextPath}/.resources/magnolia-vanity-url/desc.gif) no-repeat right;
            }
        </style>
    </head>
    <body id="mgnl">
        <h3>${this.messages.get("page.headline")}</h3>
[#assign uriList = this.uriListOfVanityUrl]
[#if uriList?has_content]
        <table class="data">
            <tr><th>${this.messages.get("page.vanity")}</th><th>${this.messages.get("page.handle")}</th></tr>
[#list uriList?keys as key]
            <tr><td>${uriList[key]}</td><td>${key}</td></tr>
[/#list]
        </table>
[#else]
        <p>${this.messages.get("page.noUrl")}</p>
[/#if]
    </body>
</html>