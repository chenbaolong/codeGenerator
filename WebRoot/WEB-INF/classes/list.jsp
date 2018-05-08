<%@ page contentType="text/html;charset=UTF-8"%>

<!DOCTYPE html>
<html>
<head> 
	<title>列表页</title>
	
	<%@include file="/jsp/base/base.jsp" %>
	
	<script language="javascript">
	function add() {
		<className></className>Form.action="<className></className>Edit.do";
		<className></className>Form.submit();
	}
	
	function del(id) {
		 if(confirm('请确认是否要删除?')){
			<className></className>Form.action="./<className></className>Dele.do?id="+id;
			<className></className>Form.submit();			
		}
	}
	
	function query(){
		if(true){
			<className></className>Form.pageno.value="1";
			<className></className>Form.action="<className></className>List.do";
			<className></className>Form.submit();
		}
	}
	
	//分页用
	function formsubmit(){
		<className></className>Form.action="<className></className>List.do";
		<className></className>Form.submit();
	
	}
	</script>
</head>
<bean:define id="basePage" name="<className></className>Form" />
<body leftmargin="0" topmargin="0" class="body">

<html:form method="post" action="<className></className>List.do" styleClass="form-horizontal">
	<div style="margin-top:15px;">
	    
	    <div class="form-actions span3" style="width:115px; margin-bottom:10px; text-align:right; border:0px red solid;">
          <input name="Ok" type="button" class="button button-small button-info" id="Ok" onClick="query()" value=" 查询 ">
          <input type="button" class="button button-small button-info" onclick="javascript: Form.clearFormEl();" value=" 清空 ">
        </div>
        
        <div style="clear:both"></div>
    </div>

	<div style="border: 0px solid #fff; margin: 5px;">
		<table class="table table-bordered">
			<thead>
				<tr>
					<td colspan="8" style="text-align: left;">
						<input name="Ok" type="button" class="button" id="Ok" onClick="add();" value=" 添加 ">&nbsp;&nbsp;
					</td>
				</tr>
				<tr>
			        <td style="text-align: center; width: 5%">序 号</td>
			        <contentComment>
						<td style="text-align: center; width: 10%"><commentTd></commentTd></td>
			        </contentComment>
					<td style="text-align: center; width: 10%">操 作</td> 
		        </tr>
			</thead>
			<tbody>
			<logic:equal name="<className></className>Form" property="isHaveRecords" value="2">
				<tr> 
					<td style="color:#ff0000" colspan="8">&nbsp;<bean:message key="message.prompt.waitfind"/></td>
				</tr>
	        </logic:equal>
	        <logic:equal name="<className></className>Form" property="isHaveRecords" value="0">
	        	<tr> 
	            	<td  style="color:#ff0000" colspan="8">&nbsp;<bean:message key="message.prompt.notfound"/></td>
	        	</tr>
	        </logic:equal>
	        <logic:equal name="<className></className>Form" property="isHaveRecords" value="1">
	        <logic:notEmpty name="<className></className>Form" property="list">
	        <logic:iterate id="element" name="<className></className>Form" property="list" indexId="index"> 
	        <tr>
	       	    	<td style="vertical-align: middle; text-align: center;">${index+1 }</td>
        		<contentField>
					<td style="vertical-align: middle; text-align: center;">${element.<fieldTd></fieldTd> }</td>
		        </contentField>
		           <td style="vertical-align: middle; text-align: center;"><div align="center">
		          		<a href="javascript:void(0);" onclick="Form.action('<%=path %>/<className></className>Edit.do?id=${element.id}');" >【编辑】</a>
		          		<a href="javascript:void(0);" onclick="Form.action('<%=path %>/<className></className>Disp.do?id=${element.id}');" >【详情】</a>
						<a href="javascript:void(0);" onclick="del('${element.id}');" >【删除】</a>
		           </td>
	        </tr>
	        </logic:iterate>
	        </logic:notEmpty>
	        </logic:equal>
	        </tbody>
	        
		</table>
		
		<%@ include file="/jsp/common/pageinfo.jsp" %>
	</div>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td><img src="<%=path%>/images/white.gif" width="4" height="4"></td>
		</tr>
	</table>
</html:form>
</body>
</html>
<logic:present name="updateSuccess">
			<script language="javascript">
			alert("用户同步成功")
			</script>
</logic:present>
<logic:present name="updateFail">
			<script language="javascript">
			alert("用户同步失败")
			</script>
</logic:present>

