
<%@ page import="activos.Activo" %>
<!doctype html>
<html>
	<head>
		<meta name="layout" content="main">
		<g:set var="entityName" value="${message(code: 'activo.label', default: 'Activo')}" />
		<title><g:message code="default.list.label" args="[entityName]" /></title>
	</head>
	<body>
		<a href="#list-activo" class="skip" tabindex="-1"><g:message code="default.link.skip.label" default="Skip to content&hellip;"/></a>
		<div class="nav" role="navigation">
			<ul>
				<li><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></li>
				<li><g:link class="create" action="nuevo"><g:message code="default.new.label" args="[entityName]" /></g:link></li>
			</ul>
		</div>
		<div id="list-activo" class="content scaffold-list" role="main">
			<h1><g:message code="default.list.label" args="[entityName]" /></h1>
			<g:if test="${flash.message}">
			<div class="message" role="status">${flash.message}</div>
			</g:if>
			<table>
				<thead>
					<tr>
					
						<g:sortableColumn property="folio" title="${message(code: 'activo.folio.label', default: 'Folio')}" />
					
						<g:sortableColumn property="procedencia" title="${message(code: 'activo.procedencia.label', default: 'Procedencia')}" />
					
						<g:sortableColumn property="factura" title="${message(code: 'activo.factura.label', default: 'Factura')}" />
					
						<g:sortableColumn property="pedimento" title="${message(code: 'activo.pedimento.label', default: 'Pedimento')}" />
					
						<g:sortableColumn property="moneda" title="${message(code: 'activo.moneda.label', default: 'Moneda')}" />
					
						<g:sortableColumn property="tipoCambio" title="${message(code: 'activo.tipoCambio.label', default: 'Tipo Cambio')}" />
					
					</tr>
				</thead>
				<tbody>
				<g:each in="${activos}" status="i" var="activo">
					<tr class="${(i % 2) == 0 ? 'even' : 'odd'}">
					
						<td><g:link action="ver" id="${activo.id}">${fieldValue(bean: activo, field: "folio")}</g:link></td>
					
						<td>${fieldValue(bean: activo, field: "procedencia")}</td>
					
						<td>${fieldValue(bean: activo, field: "factura")}</td>
					
						<td>${fieldValue(bean: activo, field: "pedimento")}</td>
					
						<td>${fieldValue(bean: activo, field: "moneda")}</td>
					
						<td>${fieldValue(bean: activo, field: "tipoCambio")}</td>
					
					</tr>
				</g:each>
				</tbody>
			</table>
			<div class="pagination">
				<g:paginate total="${totalDeActivos}" />
			</div>
		</div>
	</body>
</html>
