$('ul.nav a').filter(function() {
    return this.href == location;
}).parent().addClass('active');