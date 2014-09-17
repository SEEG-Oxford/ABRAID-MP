<#--
    Macro for including consistent bootstrap forms. For use with BaseFormViewModel.
    Copyright (c) 2014 University of Oxford
-->
<#macro form formId buttonStandardText="Save" buttonSubmittingText="Saving ...">
<form id="${formId}" action="" data-bind="formSubmit: submit">
    <#nested/>
    <p class="form-group">
        <button type="submit" class="btn btn-primary disabled" data-bind="formButton: { submitting: '${buttonSubmittingText}', standard: '${buttonStandardText}' }" disabled>&nbsp;</button>
    </p>
    <div class="form-group" data-bind="foreach: notices">
        <div data-bind="alert: $data"></div>
    </div>
</form>
</#macro>

<#macro formGroupGeneric id label glyph inputGroupOptions='class="input-group"'>
<p class="form-group">
    <label for="${id}">${label}: </label>
     <span ${inputGroupOptions}>
         <span class="input-group-addon">
            <i class="${glyph}"></i>
         </span>
         <#nested/>
    </span>
</p>
</#macro>

<#macro formGroupBasic id label bind glyph type="text">
    <@formGroupGeneric id label glyph>
        <input id="${id}" type="${type}" class="form-control" placeholder="${label}" data-bind="formValue: ${bind}" autocomplete="off">
    </@formGroupGeneric>
</#macro>

<#macro formGroupFile id label bind>
    <@formGroupGeneric id label "glyphicon glyphicon-paperclip" 'class="fileinput fileinput-new input-group" data-provides="fileinput"'>
        <span class="form-control" data-trigger="fileinput">
            <span class="fileinput-filename"></span>
        </span>
        <span class="input-group-addon btn btn-default btn-file">
            <span>Select file</span>
            <input type="file" name="file" id="file-picker" placeholder="Choose a file" autocomplete="off" data-bind="formFile: ${bind}, useFormData: useFormData">
        </span>
    </@formGroupGeneric>
</#macro>
