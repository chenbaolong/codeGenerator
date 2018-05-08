<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE html>
<html>
<head>

	<title>编辑</title>
	
	<%@include file="/jsp/base/base.jsp" %>
</head>
<body>
<bean:define id="basePage" name="<className></className>Form" />
<html:form method="post" action="<className></className>Save.do" styleClass="form-horizontal form-horizontal-simple">
	<div class="rightinfo">
	    	<div class="formtitle"><span>信息</span></div>

	    	<contentRow>
	            <div class="row">
	            	<contentInput>
		              <div class="control-group span8">
		                <label class="control-label">&nbsp;&nbsp;<s></s><commentTd></commentTd>：</label>
		                <div class="controls">
		                	${<className></className>Form.<fieldTd></fieldTd> }
		                </div>
		              </div>
	            	</contentInput>
	            </div>
	    	</contentRow>	    	
	    	
			<div class="row form-actions " style="margin-top: 150px;">
				<div class="span13 offset3">
					<input type="button" class="button" value="返 回" onclick="javascript:history.go(-1);"/>
				</div>
			</div>
	</div>
</html:form>
	
</body>
</html>