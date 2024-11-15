// Removing "required" tag if one of the inputs is not empty.
document.addEventListener('DOMContentLoaded', function() {
  const inputs = Array.from(
    document.querySelectorAll('input[name=title], input[name=body]')
  );

  const inputListener = e => {
    inputs
      .filter(i => i !== e.target)
      .forEach(i => (i.required = !e.target.value.length));
  };

  inputs.forEach(i => i.addEventListener('input', inputListener));
});

document.addEventListener('DOMContentLoaded', function () {
    const typeSelect = document.getElementById('type');
    const titleField = document.getElementById('titleField');
    const bodyField = document.getElementById('bodyField');
    const idField = document.getElementById('idField');

    // Function to hide/show the title field based on the selected type
    function updateTitleFieldVisibility() {
        if (typeSelect.value === 'post') {
            titleField.style.display = 'none';
            idField.style.display = 'none';
            bodyField.style.display = 'block';
        }
        else if (typeSelect.value === 'id') {
            titleField.style.display = 'none';
            bodyField.style.display = 'none';
            idField.style.display = 'block';
        }
        else {
            titleField.style.display = 'block';
            bodyField.style.display = 'block';
            idField.style.display = 'none';
        }
    }

    // Initial check when the page loads
    updateTitleFieldVisibility();

    // Check whenever the type selection changes
    typeSelect.addEventListener('change', updateTitleFieldVisibility);
});

function setActionAndSubmit(action) {
    const confirmAction = confirm(`Are you sure you want to ${action} these items?`);
    if (!confirmAction) return;

    document.getElementById('actionField').value = action;

    const forms = ['threadsForm', 'postsForm'];
    for (const formId of forms) {
        const form = document.getElementById(formId);
        if (form) {
            form.submit();
            break;
        }
    }
}

function toggleCheckboxes(source) {
    checkboxes = document.getElementsByName('itemIds');
    for (var i = 0, n = checkboxes.length; i < n; i++) {
        checkboxes[i].checked = source.checked;
    }
}