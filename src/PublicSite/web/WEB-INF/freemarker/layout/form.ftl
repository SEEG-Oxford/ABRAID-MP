<#--
    Macro for including consistent bootstrap forms. For use with BaseFormViewModel.
    Copyright (c) 2014 University of Oxford
-->
<#macro form formId buttonStandardText="Save" buttonSubmittingText="Saving ...">
<form id="${formId}" action="" data-bind="formSubmit: submit">
    <#nested/>
    <p class="form-group">
        <button type="submit" class="btn btn-primary" data-bind="formButton: { submitting: '${buttonSubmittingText}', standard: '${buttonStandardText}' }"></button>
    </p>
    <div class="form-group" data-bind="foreach: notices">
        <div data-bind="alert: $data"></div>
    </div>
</form>
</#macro>

<#macro formGroupBasic id label bind glyph type="text">
<p class="form-group">
    <label for="${id}">${label}: </label>
    <span class="input-group">
        <span class="input-group-addon">
            <i class="${glyph}"></i>
        </span>
        <input id="${id}" type="${type}" class="form-control" placeholder="${label}" data-bind="formValue: ${bind}">
    </span>
</p>
</#macro>
