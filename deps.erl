-module(deps).

-compile(export_all).

main(_Args) ->
	{ok, [All]} = file:consult("files.txt"),
	process(All),
	ok.

process(L) ->
	Fun = fun({application, A, P}) ->
			D1 = proplists:get_value(runtime_dependencies, P),
			D2 = proplists:get_value(applications, P),
			D = case D1 of
				undefined -> D2;
				_ -> [cvt(E) || E<-D1, is_list(E)]
			end,
			{A, D}
		end,
	L1 = lists:map(Fun, L),
	G = graph(L1),
	io:format("~s~n", [G]),

	GG = digraph:new(),
	[digraph:add_vertex(GG, V) || {V,_}<-L1],
	[[digraph:add_edge(GG,V, V1) || V1<-W] ||{V,W}<-L1],

	EE = [ X || X<-digraph:vertices(GG)],
	EE1 = [ {digraph:in_degree(GG, Z), Z} || Z<-EE ],

	io:format("~p~n", [lists:sort(EE1)]),

	ok.

cvt(E) ->
	{Z1, Z2} = lists:splitwith(fun(X) -> X/=$- end, E),
	list_to_atom(Z1).

graph(L) ->
	lists:flatten("digraph {\ngraph[ overlap=false; splines=true; ]; \n"++[graph1(X)||X<-L]++"\n}\n").

graph1({N, D}) ->
	[atom_to_list(N), " -> {", [[atom_to_list(X), "; " ] || X<-D], "}\n"].

