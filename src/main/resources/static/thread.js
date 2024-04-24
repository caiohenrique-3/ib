function populateReplyForm(postId, postBody) {
    document.getElementById('replyBody').value = '';
    document.getElementById('parentId').value = postId;
    var replyingToText = document.getElementById('replyingToText');
    replyingToText.textContent = 'Replying to: ' + postId;
    document.getElementById('replyingTo').style.display = 'block';
    highlightPost(postId);
}

function clearReplyingTo() {
    document.getElementById('replyingTo').style.display = 'none';
    document.getElementById('replyingToText').textContent = '';
    document.getElementById('parentId').value = '';
    highlightPost(null);
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