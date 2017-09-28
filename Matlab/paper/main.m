% 메인 프로그램

data_path = 'C:\paper\data';
seq_path = 'C:\paper\seq';
LOAD_SEQ = true;
SAVE_SEQ = true;


lists = dir(data_path);
n = length(lists);

testNum = 50;
portion = 0.2;


resultCell = cell(1,7);
%1에는 파일명
%2에는 평균
%3에는 표준편차
%4에는 시퀸스
%5knn
%6nb
%7dt



for k = 3:3
    file_name = strcat(data_path,"\",lists(k).name);
    resultCell(k-2,1) = {file_name};
    data = load(file_name);
    
    seq_file = strcat(seq_path,"\",strrep(lists(k).name,'.mat',''),"_seq.mat");
    if (LOAD_SEQ==true && (exist(seq_file,'file')~=0))
        load(seq_file);
        resultCell(k-2,4)= {temp};
        
    else
        temp = SeqGen(testNum,size(data.X,1),portion);
        resultCell(k-2,4)= {temp};
        if (SAVE_SEQ==true)
            save(seq_file,'temp');
        end        
    end
    
    
    
    
    result = knnClassifier(data.X,data.Y,resultCell{k-2,4});
    resultA = strcat(num2str(mean(result(:,1))), "±", num2str(std(result(:,1))));
    resultCell(k-2,5) = {resultA};
    
    result = nbClassifier(data.X,data.Y,resultCell{k-2,4});
    resultA = strcat(num2str(mean(result(:,1))), "±", num2str(std(result(:,1))));
    resultCell(k-2,6) = {resultA};
    
    result = dtClassifier(data.X,data.Y,resultCell{k-2,4});
    resultA = strcat(num2str(mean(result(:,1))), "±", num2str(std(result(:,1))));
    resultCell(k-2,7) = {resultA};
    
end


