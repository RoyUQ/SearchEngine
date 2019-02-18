<%--
  Created by IntelliJ IDEA.
  User: admin_djl
  Date: 2018/10/17
  Time: 11:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Google</title>
    <script type="text/javascript" src="jquery-3.3.1.js"></script>
</head>
<script type="text/javascript">
    $(function () {
        $('#s').click(function () {
            var query = $("#q").val();
            $.post("JqueryAjaxServlet", {
                    query: query
                },
                function (data, textStatus) {
                    var container = $('#span');
                    var resultData = JSON.parse(data);
                    container.html(resultData.result);
                });
        });
    });
</script>
<style type="text/css">
    #main {
        margin: 0 auto;
        width: 1000px;
        height: 1000px;
    }

    #searchBox {
        position: relative;
        margin: 10px auto;
    }
    #span{
        white-space: nowrap;
    }

</style>
<body>
<div id="main">
    <img src="google.png" width="200px" height="100px">
    <div id="searchBox">
        <form id="f">
            <input type="text" title="Search" name="q" id="q" maxlength="500"
                   size="80"/>
            <button id = "s" type="button">Search</button>
        </form>
    </div>
    <span id="span">

    </span>
</div>
</body>
</html>
