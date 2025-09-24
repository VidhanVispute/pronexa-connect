document.addEventListener('DOMContentLoaded', () => {
    const darkModeToggle = document.getElementById('darkModeToggle');
    if (!darkModeToggle) return;

    const toggleCircle = darkModeToggle.querySelector('div');
    const sunIcon = darkModeToggle.querySelector('.sun-icon');
    const moonIcon = darkModeToggle.querySelector('.moon-icon');

    function setTheme(theme) {
        if (theme === 'dark') {
            document.documentElement.classList.add('dark');
            toggleCircle.style.transform = 'translateX(100%)';
            sunIcon.style.opacity = '0';
            moonIcon.style.opacity = '1';
        } else {
            document.documentElement.classList.remove('dark');
            toggleCircle.style.transform = 'translateX(0)';
            sunIcon.style.opacity = '1';
            moonIcon.style.opacity = '0';
        }
        localStorage.setItem('theme', theme);
    }

    // Apply saved theme immediately on page load
    const savedTheme = localStorage.getItem('theme') || 'light';
    setTheme(savedTheme);

    // Remove the "no transition" hack after page load
    document.documentElement.setAttribute('data-loaded', 'true');

    darkModeToggle.addEventListener('click', () => {
        const newTheme = document.documentElement.classList.contains('dark') ? 'light' : 'dark';
        setTheme(newTheme);
    });
});