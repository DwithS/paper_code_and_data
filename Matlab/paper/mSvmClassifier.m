%Multiclass svm classifier
function res = mSvmClassifier(data, answer ,sim_seq)

sim_num = size(sim_seq, 2);
res = zeros(sim_num, 1);

answers = unique(answer);




for i = 1:sim_num
    tr_data = data(sim_seq(:,i), :);
    tr_ans = answer(sim_seq(:,i), :);
    
    ts_data = data(~sim_seq(:,i),:);
    ts_ans = answer(~sim_seq(:,i),:);
    
    

    
    mdl = fitcecoc(tr_data, tr_ans);
    pre = mdl.predict(ts_data);

    acc = sum(pre == ts_ans) / size(ts_ans, 1);
    res(i,1) = acc;
end

end