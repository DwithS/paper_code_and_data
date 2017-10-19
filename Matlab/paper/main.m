% �� ������ �ӽŷ��� ���� ���α׷�
clear all; clc;

% parfor�� ����ϱ� ���� p = parpool(������ ����)�� ���.
p = parpool(4); 

NumofAlgs = 3;

warning off;

data_path = 'data';
seq_path = 'seq';
LOAD_SEQ = false;
SAVE_SEQ = true;


lists = dir(data_path);
lists = lists(3:length(lists),1);
sizeofdataLists = length(lists);
testNum = 50;
portion = 0.2;


resultCell = cell(sizeofdataLists,8);
%1���� ���ϸ�
%2���� ���
%3���� ǥ������
%4���� ������
%5knn
%6nb
%7dt


start = clock; %���۽ð� ���


for k = 1:sizeofdataLists
    file_name = strcat(data_path,"\",lists(k).name);
    resultCell(k,1) = {file_name};
    data = load(file_name);
    
    seq_file = strcat(seq_path,"\",strrep(lists(k).name,'.mat',''),"_seq.mat");
    if (LOAD_SEQ==true && (exist(seq_file,'file')~=0))
        temp = load(seq_file);
        resultCell(k,4)= {temp.data};
        
    else
        temp = SeqGen(testNum,size(data.X,1),portion);
        resultCell(k,4)= {temp};
        if (SAVE_SEQ==true)
            save(seq_file,'data');
        end        
    end
end

answer = cell(11,1);

for i = 1:sizeofdataLists
   answer(i,1) = {["0" "0" "0" "0"]};
end

ppm = ParforProgMon('���൵... ', 1);
parfor k = 1:sizeofdataLists
    data = load(resultCell{k,1});
    
    temp = ["0" "0" "0" "0"];
    for i = 1:NumofAlgs
        
        result = classifer(data.X, data.Y , resultCell{k,4},i);
        resultA = strcat(num2str(mean(result(:,1))), "��", num2str(std(result(:,1))));
        temp(i) = resultA;
        
    end
    answer(k) = {temp};
    
    ppm.increment();
end

for i = 1:sizeofdataLists
    for k=1:NumofAlgs
        temp = answer{i}(k);
        resultCell(i,4+k) = {temp};
    end
end


save('RESULT.mat','resultCell');

warning on;
delete(p)
