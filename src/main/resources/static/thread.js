function populateReplyForm(postId, postBody) {
    document.getElementById('replyBody').value = '';
    document.getElementById('parentId').value = postId;
    var replyingTo = document.getElementById('replyingTo');
    replyingTo.textContent = 'Replying to: ' + postId;
    replyingTo.style.display = 'block';
    highlightPost(postId);
}

function clearReplyingTo() {
    if (confirm('Are you sure you want to clear the reply?')) {
        document.getElementById('replyingTo').style.display = 'none';
        document.getElementById('replyingToText').textContent = '';
        highlightPost(null);
    }
}

function highlightPost(postId) {
    var postElements = document.querySelectorAll('div[data-post-id]');
    postElements.forEach(function(postElement) {
        if (postElement.getAttribute('data-post-id') === postId) {
            postElement.classList.add('highlight');
        } else {
            postElement.classList.remove('highlight');
        }
    });
}