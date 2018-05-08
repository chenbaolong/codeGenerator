<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE html>
<html>
<head>

	<title>测评结果编辑</title>
	
	<%@include file="/jsp/base/base.jsp" %>
	
	
	<script language="javascript">
			function add() {
				<className></className>Form.action="<className></className>Save.do";
				<className></className>Form.submit();
			}
			
			function checkNumber(id){
				var reg = new RegExp("^[0-9]*$"); 
		    	if(!reg.test($("#"+id).val())){  
			        alert("请输入整数!"); 
			        $("#"+id).focus();
					return false; 
			    }
			    return true;
			}
			
			function checkNull(id,message){
				var val = $("#"+id).val();
				if(val==null || ""==val){
					alert(message+"不能为空！");
					$("#"+id).focus();
					return false;
				}
				return true;
			}
	</script>
	
</head>
<body>
<bean:define id="basePage" name="<className></className>Form" />
<html:form method="post" action="<className></className>Save.do" styleClass="form-horizontal form-horizontal-simple">
	<div class="rightinfo">
	    	<div class="formtitle"><span>测评结果信息</span></div>
	    	<input type="hidden" name="id" value="${<className></className>Form.id}"/>
	    	<contentRow>
	            <div class="row">
	            	<contentInput>
		              <div class="control-group span8">
		                <label class="control-label">&nbsp;&nbsp;<s>*</s><commentTd></commentTd>：</label>
		                <div class="controls">
		                	<input class="control-text" name="<fieldTd></fieldTd>" id="<fieldTd></fieldTd>" value="${<className></className>Form.<fieldTd></fieldTd> }">
		                </div>
		              </div>
	            	</contentInput>
	            </div>
	    	</contentRow>
            
			<div class="row form-actions " style="margin-top: 150px;">
				<div class="span13 offset3">
					<input type="button" class="button button-primary" id="submitbtx" value=" 提交 " onclick="add();"/>
					<input type="button" class="button" value="返 回" onclick="javascript:history.go(-1);"/>
				</div>
			</div>
	</div>
</html:form>
	
</body>
</html>