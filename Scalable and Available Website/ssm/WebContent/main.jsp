<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
<h5>yl2458   Version ${SessionVersion}   Date ${lastactiveTime}</h5>

<form method="post" action="SessionServlet">

    <legend>${message}</legend>
    <input type="submit" value="REPLACE" name="behavior" />
    <input type="text" name="replacedText" />
    
    <br>
    <input type="submit" value="REFRESH" name="behavior" />
    <br>
    <input type="submit" value="LOGOUT" name="behavior" />
  
  
</form>
<p>Cookie  ${cookieMessage} </p>  
<p>Expires ${expirationDate} </p>
<p>MetaData ${metaData} </p>
<p>RebootNumber ${rebootNum} </p> 
<%-- <p> Found session in ${sessionLocation}</p> --%>

</body>
</html>