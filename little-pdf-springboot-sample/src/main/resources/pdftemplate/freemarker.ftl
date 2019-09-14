<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <title>${title}</title>
    <link rel="stylesheet" type="text/css" href="classpath:pdftemplate/css/pdf.css"/>
</head>
<body>
<h1>
    ${title}
</h1>
<div>
    ${content}
</div>
<img src="classpath:pdftemplate/img/css.png" height="300px" width="300px"/>
<img src="https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=3190441126,995644236&fm=26&gp=0.jpg" height="300px" width="300px"/>
<ul>
    <#list list as item>

    <li>${item}</li>

    </#list>
</ul>
</body>
</html>