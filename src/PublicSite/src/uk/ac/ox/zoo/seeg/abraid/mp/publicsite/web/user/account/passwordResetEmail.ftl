<#--
    A template email sent in response to password reset requests.
    Copyright (c) 2014 University of Oxford
-->
Hello,

Somebody recently asked to reset your ABRAID-MP password. Please follow the link below to set a new password.

${url}/account/reset/process?id=${id?url('ISO-8859-1')}&key=${key?url('ISO-8859-1')}

If you don't use this link within 24 hours, it will expire. If you didn't request a password reset, please ignore this email.

Best wishes,
The ABRAID-MP Team
