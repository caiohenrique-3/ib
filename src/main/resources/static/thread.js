function populateReplyForm(postId, postBody) {
    document.getElementById('replyBody').value = '';
    document.getElementById('parentId').value = postId;
    var replyingTo = document.getElementById('replyingTo');
    replyingTo.textContent = 'Replying to: ' + postId;
    replyingTo.style.display = 'block';
    }

function clearReplyingTo() {
    if (confirm('Are you sure you want to clear the reply?')) {
        document.getElementById('replyingTo').style.display = 'none';
        document.getElementById('replyingToText').textContent = '';
        }
    }
