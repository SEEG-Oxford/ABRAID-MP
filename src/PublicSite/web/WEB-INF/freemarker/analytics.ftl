<script>
    // Setup temporary Google Analytics objects.
    window.GoogleAnalyticsObject = "ga";
    window.ga = function () { (window.ga.q = window.ga.q || []).push(arguments); };
    window.ga.l = 1 * new Date();

    // Setup analytics account
    window.ga("create", "${googleAnalyticsKey}", {
        "cookieDomain": window.location.hostname
    });
</script>
