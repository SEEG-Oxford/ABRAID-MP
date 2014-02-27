// Highlight the link for the current page
$('ul.nav a').filter(function() {
    return this.href == location;
}).parent().addClass('active');