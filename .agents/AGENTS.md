# Agent Rules

- 파일을 수정하거나 삭제할 때는 채팅창으로 미리 물어보거나 텍스트로 코드를 출력하지 말고, 즉시 파일 수정 툴(replace_file_content, write_to_file 등)을 호출하여 IDE 내장 'Changes Overview' UI가 뜨도록 해야 한다. 사용자가 해당 UI에서 승인/거절을 직접 진행한다.
