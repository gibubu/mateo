<%@ page import="general.Proveedor" %>



<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'nombre', 'error')} required">
  <label for="nombre">
    <g:message code="proveedor.nombre.label" default="Nombre" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="nombre" maxlength="64" required="" value="${proveedor?.nombre}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'nombreCompleto', 'error')} required">
  <label for="nombreCompleto">
    <g:message code="proveedor.nombreCompleto.label" default="Nombre Completo" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="nombreCompleto" maxlength="128" required="" value="${proveedor?.nombreCompleto}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'rfc', 'error')} required">
  <label for="rfc">
    <g:message code="proveedor.rfc.label" default="Rfc" />
    <span class="required-indicator">*</span>
  </label>
  <g:textField name="rfc" maxlength="13" required="" value="${proveedor?.rfc}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'curp', 'error')} ">
  <label for="curp">
    <g:message code="proveedor.curp.label" default="Curp" />

  </label>
  <g:textField name="curp" maxlength="18" value="${proveedor?.curp}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'direccion', 'error')} ">
  <label for="direccion">
    <g:message code="proveedor.direccion.label" default="Direccion" />

  </label>
  <g:textArea name="direccion" cols="40" rows="5" maxlength="500" value="${proveedor?.direccion}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'telefono', 'error')} ">
  <label for="telefono">
    <g:message code="proveedor.telefono.label" default="Telefono" />

  </label>
  <g:textField name="telefono" maxlength="25" value="${proveedor?.telefono}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'fax', 'error')} ">
  <label for="fax">
    <g:message code="proveedor.fax.label" default="Fax" />

  </label>
  <g:textField name="fax" maxlength="25" value="${proveedor?.fax}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'contacto', 'error')} ">
  <label for="contacto">
    <g:message code="proveedor.contacto.label" default="Contacto" />

  </label>
  <g:textField name="contacto" maxlength="64" value="${proveedor?.contacto}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'correo', 'error')} ">
  <label for="correo">
    <g:message code="proveedor.correo.label" default="Correo" />

  </label>
  <g:field type="email" name="correo" maxlength="128" value="${proveedor?.correo}"/>
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'base', 'error')} ">
  <label for="base">
    <g:message code="proveedor.base.label" default="Base" />

  </label>
  <g:checkBox name="base" value="${proveedor?.base}" />
</div>

<div class="fieldcontain ${hasErrors(bean: proveedor, field: 'empresa', 'error')} required">
  <label for="empresa">
    <g:message code="proveedor.empresa.label" default="Empresa" />
    <span class="required-indicator">*</span>
  </label>
  <g:select id="empresa" name="empresa.id" from="${general.Empresa.list()}" optionKey="id" required="" value="${proveedor?.empresa?.id}" class="many-to-one"/>
</div>

